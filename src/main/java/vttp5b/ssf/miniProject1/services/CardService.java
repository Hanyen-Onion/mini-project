package vttp5b.ssf.miniProject1.services;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    // //cache search result
    // public void cacheAddrList(List<DayItinerary>) {
    //     pRepo.cacheAddrList(null);
    // }
    
    public List<DayItinerary> getCachedMaps(AddressSearchParams params) {
           
        Set<String> redisList = pRepo.retrieveAddrList();
        List<DayItinerary> addrList = new LinkedList<>();

        if ((redisList == null)||(redisList.isEmpty())) {
            return null;
        }

        redisList.stream().forEach(o -> {
            //parse
            DayItinerary obj = DayItinerary.parseToAddrObj(o);
            addrList.add(obj);
        });
        return addrList;
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

        //build json to send to google
        JsonObject json = Json.createObjectBuilder()
            .add("textQuery", param.query())
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
            String lat = resultObj.getJsonObject("location").getString("latitude", null);
            String lon = resultObj.getJsonObject("location").getString("longitude", null);
            String googleMapUrl = resultObj.getString("googleMapsUri", null);
            String placeId = resultObj.getString("id", null);

            //add to object
            DayItinerary dayObj = new DayItinerary();
            dayObj.setAddress(address);
            dayObj.setDisplayName(displayName);
            dayObj.setGoogleMapUrl(googleMapUrl);
            dayObj.setLat(lat);
            dayObj.setLon(lon);
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
