package vttp5b.ssf.miniProject1.models;

import java.util.LinkedList;
import java.util.List;

public class DayItinerary {

    private String address;
    private String displayName;
    private String placeId;
    private String embedMapUrl;

    private String time;
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

    public String getPlaceId() {    return placeId;}
    public void setPlaceId(String placeId) {    this.placeId = placeId;}

    public static String itinListKey(String date) {

        if (date == null) {
            return null;
        }
        String key = "d:" + date;
        return key;
    }

    public static List<String> parseKeysToList(String string, User user) {
        
        if (string == null) {
            System.out.println("cannot find dayList");
            return null;
        }
        String[] field = string.replaceAll("d:", "").split(",");
        
        List<String> keys = new LinkedList<>();

        for (int i = 0; i < field.length; i++) {
            keys.add(field[i]);
        }
        return keys;
    }

    public static DayItinerary parseToAddrObj(String string) {
        DayItinerary itin = new DayItinerary();

        if (string == null) {
            System.out.println("string is null");
            return null;
        }

        String[] fields = string.split("&");
        String[] kv;
        String str;
        
        for(int i = 0; i < fields.length; i++) {
            kv = fields[i].split("=");
   
            if (kv.length == 2) 
            switch (kv[0]) {
                case "time":
                    str = kv[1].replace("nullT", "");
                    itin.setTime(str);
                    break;
                case "address":
                    itin.setAddress(kv[1]);
                    break;
                case "displayName":
                    itin.setDisplayName(kv[1]);
                    break;
                case "embedMapUrl":
                    itin.setEmbedMapUrl(kv[1]);
                    break;
                case "placeId":
                    itin.setPlaceId(kv[1]);
                    break;
                case "date":
                    itin.setDate(kv[1]);
                    break;
                default:
                    break;
            }
        }
        return itin;
    }

    @Override
    public String toString() {
        return "time=" + time + "&address=" + address + "&displayName=" + displayName + "&placeId=" + placeId + "&embedMapUrl=" + embedMapUrl ;
    }
}
