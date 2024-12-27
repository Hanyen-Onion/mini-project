package vttp5b.ssf.miniProject1.services;

import static vttp5b.ssf.miniProject1.Util.parseBackTime;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
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
import vttp5b.ssf.miniProject1.models.AddressSearchParams;
import vttp5b.ssf.miniProject1.models.DayItinerary;
import vttp5b.ssf.miniProject1.models.User;
import vttp5b.ssf.miniProject1.repositories.PlannerRepository;

@Service
public class CardService {
    
    @Autowired
    private PlannerRepository pRepo;

    @Value("${google.map.api.key}")
    private String googleApi;

    private static final String MAP_URL = "https://www.google.com/maps/embed/v1/place";
    private static final String ADDRESS_URL = "https://places.googleapis.com/v1/places:searchText";
    private static final String FIELD_MASK_PARAM = "places.formattedAddress,places.id,places.displayName.text,places.location,places.googleMapsUri";

    //get itinList for the day
    public List<DayItinerary> getItinListforTheDay(String date, User user) {
        Set<String> itinSet = pRepo.retrieveDate(date, user.getUsername());
        List<DayItinerary> itinList = new LinkedList<>();

        if (itinSet == null || itinSet.isEmpty()||user == null) {
            return null;
        }
        
        itinSet.forEach(s -> {
            //parse string to obj
            DayItinerary itin = DayItinerary.parseToAddrObj(s);
            itinList.add(itin);
        });
       
         // Sort the list by time
        itinList.sort(Comparator.comparing(itin -> {

            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            return LocalDateTime.parse(itin.getTime(), formatter); 
        }));

        itinList.forEach(obj -> {
            obj.setTime(parseBackTime(obj.getTime()));
        });

        return itinList;
    }

    // public List<List<DayItinerary>> listOfList() {

    // }

    public List<String> getDayList(String date, User user) {
        String str = pRepo.getDayListFromUser(user, DayItinerary.itinListKey(user.getUsername(), date));

        //iterate and add
        List<String> keys = DayItinerary.parseKeyToList(str, user);
        return keys;
    }


    //add itin to list for the day
    public void saveItinerary(DayItinerary itin, User user) {
        pRepo.saveToDate(itin, itin.getDate(), user.getUsername());
        //get listOfTheDays and save to userAcct
        pRepo.getListsOfDays(user.getUsername());
        pRepo.saveItineraryToRedis(user);
    }

    public DayItinerary findAddress(String displayName, String address) {
        List<DayItinerary> searchResults = getCachedAddrList(displayName);
        DayItinerary itin = new DayItinerary();
        searchResults.forEach(obj -> {
            if (obj.getDisplayName().equals(displayName)||(obj.getAddress().equals(address))) {
                itin.setAddress(obj.getAddress());
                itin.setDate(obj.getDate());
                itin.setDisplayName(obj.getDisplayName());
                itin.setEmbedMapUrl(obj.getEmbedMapUrl());
                itin.setPlaceId(obj.getPlaceId());
            }
        });

        return itin;
    }

    public List<DayItinerary> getCachedAddrList(String displayName) {
        Set<String> redisList = pRepo.retrieveTempAddrList();
        List<DayItinerary> addrList = new LinkedList<>();

        if (redisList.isEmpty() || redisList == null) {
            AddressSearchParams param = new AddressSearchParams();
            param.setQuery(displayName);
            addrList = getTextSearchApi(param);
            return addrList;
        }
            addrList = redisList.stream()
                        .map(DayItinerary::parseToAddrObj)
                        .collect(Collectors.toList());  

        return addrList;
    }

    public void cacheAddrList(List<DayItinerary> searchResult) {
        pRepo.cacheAddrSearchList(searchResult);
    }

    public String getMapEmbeddedUrl(DayItinerary day) {
        
        //build url 
        String url = UriComponentsBuilder
            .fromUriString(MAP_URL)
            .queryParam("key", googleApi)
            .queryParam("q", "place_id:" + day.getPlaceId())
            .toUriString();
        return url;
    }
    
    public List<DayItinerary> getTextSearchApi(AddressSearchParams param) {
        System.out.println(param.getQuery());
        //build json to send to google
        JsonObject json = Json.createObjectBuilder()
            .add("textQuery", param.getQuery())
            .build();
        
        //POST
        RequestEntity<String> req = RequestEntity
            .post(ADDRESS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("X-Goog-Api-Key", googleApi)
            .header("X-Goog-FieldMask", FIELD_MASK_PARAM)
            .body(json.toString(), String.class);

        RestTemplate template = new RestTemplate();

        try {
            ResponseEntity<String> resp =  template.exchange(req, String.class);
            String payload = resp.getBody();

            return getAllPlaceDetail(payload);

        } catch (Exception ex) {
            System.out.println("responseEntity got problem at MapService");
            ex.printStackTrace();
        }
        return null;
    }

    private List<DayItinerary> getAllPlaceDetail(String payload) {

        List<DayItinerary> addressInfoList = new LinkedList<>();
        
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject jsonObj = reader.readObject();
        JsonArray placesArray = jsonObj.getJsonArray("places");

        for (int i = 0; i < placesArray.size(); i++) {
            JsonObject resultObj = placesArray.getJsonObject(i);

            String address = resultObj.getString("formattedAddress", null);
            String displayName = resultObj.getString("displayName", null);
            String placeId = resultObj.getString("id", null);

            //add to object
            DayItinerary dayObj = new DayItinerary();
            dayObj.setAddress(address);
            dayObj.setDisplayName(displayName);
            dayObj.setPlaceId(placeId);

            // //get mapApi
            String eMapUrl = getMapEmbeddedUrl(dayObj);
            dayObj.setEmbedMapUrl(eMapUrl);
            //add to list
            addressInfoList.add(dayObj);
        }
        return addressInfoList;
    }

}
