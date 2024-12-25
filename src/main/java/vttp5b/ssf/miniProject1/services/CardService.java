package vttp5b.ssf.miniProject1.services;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

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

        //build url 
        JsonObject json = Json.createObjectBuilder()
            .add("textQuery", param.textQuery())
            //.add("textQuery", "10 Paya Lebar Rd, #B1-28 PLQ Mall")
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
            System.out.println("responseEntity got problem at FlightService");
            ex.printStackTrace();
        }
        return null;
    }

    private List<DayItinerary> getAllPlaceDetail(String payload) {

        List<DayItinerary> addressInfoList = new LinkedList<>();
        
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject jsonObj = reader.readObject();
        JsonArray placesArray = jsonObj.getJsonArray("places");

        System.out.println(placesArray);

        for (int i = 0; i < placesArray.size(); i++) {
            JsonObject resultObj = placesArray.getJsonObject(i);

            String address = resultObj.getString("formattedAddress");
            String displayName = resultObj.getString("displayName");
            String lat = resultObj.getJsonObject("location").getString("latitude");
            String lon = resultObj.getJsonObject("location").getString("longitude");
            String googleMapUrl = resultObj.getString("googleMapsUri");
            String placeId = resultObj.getString("id");

            //add to object
            DayItinerary dayObj = new DayItinerary();
            dayObj.setAddress(address);
            dayObj.setDisplayName(displayName);
            dayObj.setGoogleMapUrl(googleMapUrl);
            dayObj.setLat(lat);
            dayObj.setLon(lon);
            dayObj.setPlaceId(placeId);

            //add to list
            addressInfoList.add(dayObj);
        }
        return addressInfoList;
    }

}
