package vttp5b.ssf.miniProject1.services;

import static vttp5b.ssf.miniProject1.Util.*;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp5b.ssf.miniProject1.models.DayItinerary;
import vttp5b.ssf.miniProject1.models.FlightInfo;
import vttp5b.ssf.miniProject1.models.FlightSearchParams;
import vttp5b.ssf.miniProject1.models.User;
import vttp5b.ssf.miniProject1.repositories.PlannerRepository;

@Service
public class FlightService {
    
    @Autowired
    private PlannerRepository plannerRepo;

    @Value("${aviationstack.api.key}")
    private String aviationStackApi;

    private static final String FLIGHT_URL = "https://api.aviationstack.com/v1/flights";

    //retrieve flight from user acct
    public FlightInfo retrieveFlightFromAcct(User user, String hkey) {
        String f = plannerRepo.getFlight(user, hkey);
        
        if (f != null) {
            FlightInfo flight = FlightInfo.parseToFlightInfoObj(f);
            System.out.println("parsing at service" + flight);
            return flight;
        }
        return null;
    }

    //save flight to user acct in redis
    public void saveFlightToAcct(FlightInfo flight, User user, String hkey) {
        //hashkey name should be fromTo, backTo
        plannerRepo.saveFlightToRedis(user,flight, hkey);
    }

    //get a flight obj from flightList
    public FlightInfo getFlightObj(String flightcode) {
        List<FlightInfo> flightList = getFlightList();

        Optional<FlightInfo> obj = flightList.stream()
                            .filter(f -> f.getFlightCode().equals(flightcode))
                            .findAny();

        if (obj.isEmpty()) {
            return null;
        }

        FlightInfo flight = obj.get();
        return flight;
    }

    public List<FlightInfo> filter(List<FlightInfo> flightInfoList, FlightSearchParams params) {
        
        List<FlightInfo> filteredList = flightInfoList.stream()
            .filter(flight -> params.arrAirport().isBlank() || flight.getArrivalAirport().equals(params.arrAirport()))
            .filter(flight -> params.depAirport().isBlank() || flight.getDepartureAirport().equals(params.depAirport()))
            .filter(flight -> params.flightCode().isBlank() || flight.getFlightCode().equals(params.flightCode()))
            .filter(flight -> params.date().isBlank() || flight.getFlightDate().equals(params.date()))
            .collect(Collectors.toList());

        return filteredList;
    }

   public List<FlightInfo> getFlightList() {

        Set<String> redisList = plannerRepo.retrieveFlightList();
        List<FlightInfo> flightList = new LinkedList<>();

        if (redisList.isEmpty()) {
            flightList = getFlightApi();
            return flightList;
        }

        flightList = redisList.stream()
                        .map(FlightInfo::parseToFlightInfoObj)
                        .collect(Collectors.toList());  

        return flightList;
    }

    public List<FlightInfo> getFlightApi() {

        //build url 
        String url = UriComponentsBuilder
            .fromUriString(FLIGHT_URL)
            .queryParam("access_key", aviationStackApi)
            .queryParam("limit", 100)
            .queryParam("flight_status", "scheduled")
            .toUriString();
        
        //GET /flight
        RequestEntity<Void> req = RequestEntity
            .get(url)
            .accept(MediaType.APPLICATION_JSON)
            .build();

        RestTemplate template = new RestTemplate();

        try {
            ResponseEntity<String> resp =  template.exchange(req, String.class);
            String payload = resp.getBody();

            return getAllFlightInfos(payload);

        } catch (Exception ex) {
            System.out.println("responseEntity got problem at FlightService");
            ex.printStackTrace();
        }
        return null;
    }

    private List<FlightInfo> getAllFlightInfos(String payload) {

        List<FlightInfo> fInfoList = new LinkedList<>();
        
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject jsonObj = reader.readObject();
        JsonArray dataArray = jsonObj.getJsonArray("data");

        for (int i = 0; i < dataArray.size(); i++) {
            JsonObject flightData = dataArray.getJsonObject(i);

            String flyDate = flightData.getString("flight_date",null);
            String flightcode = flightData.getJsonObject("flight").getString("iata",null);
            
            JsonObject departObj = flightData.getJsonObject("departure");
            String departAirport = departObj.getString("iata",null);
            String departTimezone = departObj.getString("timezone",null);
            String departTerminal = departObj.getString("terminal",null);
            String departBoardingGate = departObj.getString("gate",null);
            String departTime = departObj.getString("scheduled",null);

            JsonObject arriveObj = flightData.getJsonObject("arrival");
            String arrivalAirport = arriveObj.getString("iata",null);
            String arrivalTerminal = arriveObj.getString("terminal",null);
            String arrivalBoardingGate = arriveObj.getString("gate",null);
            String arrivalTimezone = arriveObj.getString("timezone",null);
            String arrivalTime = arriveObj.getString("scheduled",null);

            //add to object
            FlightInfo flightInfo = new FlightInfo();
            flightInfo.setFlightDate(parseToDate(flyDate));
            flightInfo.setFlightCode(flightcode);

            flightInfo.setDepartureAirport(departAirport);
            flightInfo.setDepartureTerminal(departTerminal);
            flightInfo.setDepartureBoardingGate(departBoardingGate);
            flightInfo.setDepartureTime(parseToTime(departTime, departTimezone));

            flightInfo.setArrivalAirport(arrivalAirport);
            flightInfo.setArrivalTerminal(arrivalTerminal);
            flightInfo.setArrivalBoardingGate(arrivalBoardingGate);
            flightInfo.setArrivalTime(parseToTime(arrivalTime, arrivalTimezone));

            FlightInfo.setNoAvailable(flightInfo);

            //add to list
            fInfoList.add(flightInfo);
            //System.out.println(flightInfo);
        }
        plannerRepo.cacheFlights(fInfoList);
        return fInfoList;
    }

}

