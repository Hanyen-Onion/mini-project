package vttp5b.ssf.miniProject1.controllers;

import static vttp5b.ssf.miniProject1.Util.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import vttp5b.ssf.miniProject1.models.DayItinerary;
import vttp5b.ssf.miniProject1.models.FlightInfo;
import vttp5b.ssf.miniProject1.models.User;
import vttp5b.ssf.miniProject1.services.CardService;
import vttp5b.ssf.miniProject1.services.FlightService;
import vttp5b.ssf.miniProject1.services.SessionService;

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
        System.out.println("post flightcode for depature");

        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }
        System.out.println(user);

        //find flight through flightCode
        FlightInfo ftFlight = fSvc.getFlightObj(flightCode);
        //save flight to userAcct
        fSvc.saveFlightToAcct(ftFlight, user, FROM_TO);

        System.out.println(ftFlight);
        
        mav.addObject(USER_INFO, user);
        mav.setViewName("redirect:/travel_planner");
        return mav;
    }

    // return -> tavel_planner/return/flightcode=
    @PostMapping("travel_planner/return/{flightCode}")
    public ModelAndView postToBtPlanner(@PathVariable String flightCode, HttpSession sess){
        ModelAndView mav = new ModelAndView();
        System.out.println("post flightcode for arrivial");

        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }
        System.out.println(user);

        //find flight through flightCode
        FlightInfo btFlight = fSvc.getFlightObj(flightCode);
        //save flight to userAcct
        fSvc.saveFlightToAcct(btFlight, user, BACK_TO);

        System.out.println(btFlight);
        
        mav.addObject(USER_INFO, user);
        mav.setViewName("redirect:/travel_planner");
        return mav;
    }

    @GetMapping(path={"travel_planner"})
    public ModelAndView getTravelPlanner(HttpSession sess) {
        ModelAndView mav = new ModelAndView();
        System.out.println("get travel planner");
        
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }
        System.out.println(user);
        
        //fetch flight detail from acct
        FlightInfo ftFlight = fSvc.retrieveFlightFromAcct(user, FROM_TO);
        FlightInfo btFlight = fSvc.retrieveFlightFromAcct(user, BACK_TO);
        //daylist
       
        
        mav.addObject(USER_INFO, user);
        mav.addObject(FROM_TO, ftFlight);
        mav.addObject(BACK_TO, btFlight);
        mav.setViewName("travel_planner");
        return mav;
    }

    @GetMapping("save")
    public ModelAndView getSave(HttpSession sess) {
        ModelAndView mav = new ModelAndView();
        sess.invalidate();
        mav.setViewName("save");
        return mav;
    }
    
}
