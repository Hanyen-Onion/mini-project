package vttp5b.ssf.TravelPlanner.services;

import static vttp5b.ssf.TravelPlanner.Util.parseBackTime;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.*;
import vttp5b.ssf.TravelPlanner.models.*;
import vttp5b.ssf.TravelPlanner.repositories.PlannerRepository;

@Service
public class CardService {
    
    @Autowired
    private PlannerRepository pRepo;

    @Value("${google.map.api.key}")
    private String googleApi;

    private static final String MAP_URL = "https://www.google.com/maps/embed/v1/place";
    private static final String ADDRESS_URL = "https://places.googleapis.com/v1/places:searchText";
    private static final String FIELD_MASK_PARAM = "places.formattedAddress,places.id,places.displayName.text,places.location,places.googleMapsUri";

    public void deleteDateList(User user, String date) {
        pRepo.delhKey(user,date);
    }
    
    //hkeys key(user:username_id)
    public List<String> getDayListFromUser(User user) {
        Set<String> hkeys = pRepo.getAllhKeys(user);

        if (hkeys == null)
            return null;

        List<String> dayListHkeys = hkeys.stream()
                                        .filter(k -> k.contains("d:"))
                                        .map(k -> k.replace("d:", ""))
                                        .collect(Collectors.toList());
        return dayListHkeys;
    }
    
    //get all data
    public Map<String,List<DayItinerary>> mapList(User user) {
        List<String> dayList = getDayList(user);
        
        if (dayList == null) {
            return null;
        }
        Map<String,List<DayItinerary>> mapList = new HashMap<>();

        dayList.forEach(day -> {
            List<DayItinerary> itinList = getItinListforTheDay(day, user);
            mapList.put(day,itinList);
        });
        return mapList;
    }

    //get itinList for the day
    public List<DayItinerary> getItinListforTheDay(String date, User user) {
        Set<String> dateSet = pRepo.retrieveDateSet(date, user.getUsername());
        List<DayItinerary> itinList = new LinkedList<>();

        if (dateSet == null || dateSet.isEmpty()||user == null) {
            return null;
        }
        
        dateSet.forEach(s -> {
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

    public List<String> getDayList(User user) {

        List<String> dayList = getDayListFromUser(user);
 
        if (dayList == null) {
            return null;
        }
        return dayList;
    }

    //add itin to list for the day
    public void saveItinerary(DayItinerary itin, User user) {
        pRepo.saveToDateSet(itin, user.getUsername());
        //save dateSet to userAcct
        pRepo.saveDateToAcct(itin, user);
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
                itin.setTime(obj.getTime());
            }
        });

        return itin;
    }

    public List<DayItinerary> getCachedAddrList(String displayName) {
        Set<String> redisList = pRepo.retrieveTempAddrList();
        List<DayItinerary> addrList = new LinkedList<>();

        if (redisList.isEmpty() || redisList == null) {
            SearchParams param = new SearchParams();
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
    
    public List<DayItinerary> getTextSearchApi(SearchParams param) {
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
            dayObj.setTime(null);

            // //get mapApi
            String eMapUrl = getMapEmbeddedUrl(dayObj);
            dayObj.setEmbedMapUrl(eMapUrl);
            //add to list
            addressInfoList.add(dayObj);
        }
        return addressInfoList;
    }

}
