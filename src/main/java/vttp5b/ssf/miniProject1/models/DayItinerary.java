package vttp5b.ssf.miniProject1.models;

import java.util.List;

public class DayItinerary {
    private String time;
    private String address;
    private String displayName;
    private String lat;
    private String lon;
    private String googleMapUrl;
    private String placeId;

    private String embedMapUrl;
    private String date;

    public String getEmbedMapUrl() {    return embedMapUrl;}
    public void setEmbedMapUrl(String embedMapUrl) {    this.embedMapUrl = embedMapUrl;}
    
    public String getTime() {    return time;}
    public void setTime(String time) {    this.time = time;}

    public String getAddress() {    return address;}
    public void setAddress(String address) {    this.address = address;}

    public String getDate() {    return date;}
    public void setDate(String date) {    this.date = date;}
        
    public String getDisplayName() {    return displayName;}
    public void setDisplayName(String displayName) {    this.displayName = displayName;}
    
    public String getLat() {    return lat;}
    public void setLat(String lat) {    this.lat = lat;}

    public String getLon() {    return lon;}
    public void setLon(String lon) {    this.lon = lon;}

    public String getGoogleMapUrl() {    return googleMapUrl;}
    public void setGoogleMapUrl(String googleMapUrl) {    this.googleMapUrl = googleMapUrl;}

    public String getPlaceId() {    return placeId;}
    public void setPlaceId(String placeId) {    this.placeId = placeId;}

    //sessionId=obj1, obj2 , obj3
    //should get from the controller
    public static String arrayForEachDay() {

        //get all day Objs to list first
        List<DayItinerary> allItineraries;

        //return allItineraries.toString();
        return null;
    }

    @Override
    public String toString() {
        return "time=" + time + "&address=" + address + "&displayName=" + displayName + "&lat=" + lat
                + "&lon=" + lon + "&googleMapUrl=" + googleMapUrl + "&placeId=" + placeId + "&date=" + date + "&embedMapUrl=" + embedMapUrl ;
    }
}
