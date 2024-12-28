package vttp5b.ssf.TravelPlanner.models;

import java.lang.reflect.Field;

public class FlightInfo {
    
    private String flightCode; //IATA code
    private String flightDate;

    //departure parameters
    private String departureAirport; //airport code
    private String departureTerminal;
    private String departureBoardingGate;
    private String departureTime;
    
    //arrival parameters
    private String arrivalAirport; //where the flight lands
    private String arrivalTerminal;
    private String arrivalBoardingGate;
    private String arrivalTime;

    //to be pass to html

    //getter setter
    public String getFlightCode() {    return flightCode;}
    public void setFlightCode(String flightCode) {    this.flightCode = flightCode;}
    
    public String getDepartureAirport() {return departureAirport;}
    public void setDepartureAirport(String departureAirport) {    this.departureAirport = departureAirport;}
    
    public String getArrivalAirport() {    return arrivalAirport;}
    public void setArrivalAirport(String arrivalAirport) {    this.arrivalAirport = arrivalAirport;}
    
    public String getDepartureTerminal() {    return departureTerminal;}
    public void setDepartureTerminal(String departureTerminal) {    this.departureTerminal = departureTerminal;}

    public String getDepartureBoardingGate() {    return departureBoardingGate;}
    public void setDepartureBoardingGate(String departureBoardingGate) {    this.departureBoardingGate = departureBoardingGate;}

    public String getArrivalTerminal() {    return arrivalTerminal;}
    public void setArrivalTerminal(String arrivalTerminal) {    this.arrivalTerminal = arrivalTerminal;}

    public String getArrivalBoardingGate() {    return arrivalBoardingGate;}
    public void setArrivalBoardingGate(String arrivalBoardingGate) {    this.arrivalBoardingGate = arrivalBoardingGate;}

    public String getFlightDate() {    return flightDate;}
    public void setFlightDate(String flightDate) {    this.flightDate = flightDate;}
    
    public String getDepartureTime() {    return departureTime;}
    public void setDepartureTime(String departureTime) {    this.departureTime = departureTime;}
    
    public String getArrivalTime() {    return arrivalTime;}
    public void setArrivalTime(String arrivalTime) {    this.arrivalTime = arrivalTime;}

    //for cases when information is not provided by api
    public static void setNoAvailable(FlightInfo info)  {
        
        for(Field field : info.getClass().getDeclaredFields()){
            Object value;

            try {
                value = field.get(info);

                if (value == null) {
                field.set(info, "information not available");
                }
                //System.out.println(value); 

            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }   
        }
    }

    //parse string back to obj
    public static FlightInfo parseToFlightInfoObj(String string) {
        FlightInfo flightInfo = new FlightInfo();
        
        if (string == null) {
            System.out.println("empty flightObj cannot be parse");
            return null;
        }
        
        String[] fields = string.split("&");
        String[] kv;

        for(int i = 0; i < fields.length; i++) {
            kv = fields[i].split("=");
            
            if (kv.length == 2) 
            switch (kv[0]) {
                case "flightCode":
                    flightInfo.setFlightCode(kv[1]);
                    break;
                case "flightDate":
                    flightInfo.setFlightDate(kv[1]);
                    break;
                case "departureAirport":
                    flightInfo.setDepartureAirport(kv[1]);
                    break;
                case "departureTerminal":
                    flightInfo.setDepartureTerminal(kv[1]);
                    break;
                case "departureBoardingGate":
                    flightInfo.setDepartureBoardingGate(kv[1]);
                    break;
                case "departureTime":
                    flightInfo.setDepartureTime(kv[1]);
                    break;
                case "arrivalAirport":
                    flightInfo.setArrivalAirport(kv[1]);
                    break;
                case "arrivalTerminal":
                    flightInfo.setArrivalTerminal(kv[1]);
                    break;
                case "arrivalBoardingGate":
                    flightInfo.setArrivalBoardingGate(kv[1]);
                    break;
                case "arrivalTime":
                    flightInfo.setArrivalTime(kv[1]);
                    break;
                default:
                    System.out.println("error with flight obj parsing string");
                    break;
            }
        }
        return flightInfo;
    }

    @Override
    public String toString() {
        return "flightCode=" + flightCode + "&flightDate=" + flightDate + "&departureAirport="
                + departureAirport + "&departureTerminal=" + departureTerminal + "&departureBoardingGate="
                + departureBoardingGate + "&departureTime=" + departureTime + "&arrivalAirport=" + arrivalAirport
                + "&arrivalTerminal=" + arrivalTerminal + "&arrivalBoardingGate=" + arrivalBoardingGate
                + "&arrivalTime=" + arrivalTime;
    }

    

}
