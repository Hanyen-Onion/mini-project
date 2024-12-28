package vttp5b.ssf.miniProject1.models;

public class SearchParams {
    //address search
    private String query;

    //flight search
    private String date;
    private String flightCode;
    private String arrAirport;
    private String depAirport;
    
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getFlightCode() {
        return flightCode;
    }
    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }
    public String getArrAirport() {
        return arrAirport;
    }
    public void setArrAirport(String arrAirport) {
        this.arrAirport = arrAirport;
    }
    public String getDepAirport() {
        return depAirport;
    }
    public void setDepAirport(String depAirport) {
        this.depAirport = depAirport;
    }
}
