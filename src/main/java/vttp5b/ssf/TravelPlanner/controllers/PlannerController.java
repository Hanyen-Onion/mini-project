package vttp5b.ssf.TravelPlanner.controllers;

import static vttp5b.ssf.TravelPlanner.Util.*;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import vttp5b.ssf.TravelPlanner.models.*;
import vttp5b.ssf.TravelPlanner.services.*;

@Controller
@RequestMapping()
public class PlannerController {

    @Autowired
    private SessionService sSvc;

    @Autowired
    private FlightService fSvc;

    @Autowired
    private CardService cSvc;

    private static final String FROM_TO = "fromTo";
    private static final String BACK_TO = "backTo";

    // depart -> tavel_planner/departure/flightcode
    @PostMapping("travel_planner/depart/{flightCode}")
    public ModelAndView postFtToPlanner(@PathVariable String flightCode, HttpSession sess){
        
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }
        String username = user.getUsername();

        //find flight through flightCode
        FlightInfo ftFlight = fSvc.getFlightObj(flightCode);
        //save flight to userAcct
        fSvc.saveFlightToAcct(ftFlight, user, FROM_TO);
        
        mav.addObject(USER_INFO, user);
        mav.addObject("username", username);
        mav.setViewName("redirect:/travel_planner");
        return mav;
    }

    // return -> tavel_planner/return/flightcode=
    @PostMapping("travel_planner/return/{flightCode}")
    public ModelAndView postToBtPlanner(@PathVariable String flightCode, HttpSession sess){
        
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }
        String username = user.getUsername();

        //find flight through flightCode
        FlightInfo btFlight = fSvc.getFlightObj(flightCode);
        //save flight to userAcct
        fSvc.saveFlightToAcct(btFlight, user, BACK_TO);
        
        //daylist
        Map<String, List<DayItinerary>> mapList = cSvc.mapList(user);

        mav.addObject(USER_INFO, user);
        mav.addObject("username", username);
        mav.addObject("dayList", mapList);
        mav.setViewName("redirect:/travel_planner");
        return mav;
    }

    @GetMapping(path={"travel_planner"})
    public ModelAndView getTravelPlanner(HttpSession sess) {
        
        ModelAndView mav = new ModelAndView();  
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }

        String username = user.getUsername();
        
        //fetch flight detail from acct
        FlightInfo ftFlight = fSvc.retrieveFlightFromAcct(user, FROM_TO);
        FlightInfo btFlight = fSvc.retrieveFlightFromAcct(user, BACK_TO);
        
        //daylist
        Map<String, List<DayItinerary>> mapList = cSvc.mapList(user);
    
        mav.addObject(USER_INFO, user);
        mav.addObject(FROM_TO, ftFlight);
        mav.addObject(BACK_TO, btFlight);
        mav.addObject("username", username);
        mav.addObject("dayList", mapList);
        mav.setViewName("travel_planner");
        return mav;
    }

    @PostMapping("travel_planner")
    public ModelAndView delCard(HttpSession sess, @RequestBody String del) {
        ModelAndView mav = new ModelAndView();  
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }

        if(del.equals("delete")) {
            cSvc.deleteDateList(user, del);
        }
        String username = user.getUsername();
        
        //fetch flight detail from acct
        FlightInfo ftFlight = fSvc.retrieveFlightFromAcct(user, FROM_TO);
        FlightInfo btFlight = fSvc.retrieveFlightFromAcct(user, BACK_TO);
        
        //daylist
        Map<String, List<DayItinerary>> mapList = cSvc.mapList(user);
    
        mav.addObject(USER_INFO, user);
        mav.addObject(FROM_TO, ftFlight);
        mav.addObject(BACK_TO, btFlight);
        mav.addObject("username", username);
        mav.addObject("dayList", mapList);
        mav.setViewName("travel_planner");
        return mav;
    }
    
}
