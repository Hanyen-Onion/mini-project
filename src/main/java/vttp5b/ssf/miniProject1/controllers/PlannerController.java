package vttp5b.ssf.miniProject1.controllers;

import static vttp5b.ssf.miniProject1.Util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import vttp5b.ssf.miniProject1.models.FlightInfo;
import vttp5b.ssf.miniProject1.models.User;
import vttp5b.ssf.miniProject1.services.FlightService;
import vttp5b.ssf.miniProject1.services.SessionService;

@Controller
@RequestMapping("/travel_planner")
public class PlannerController {

    @Autowired
    private SessionService sSvc;

    @Autowired
    private FlightService fSvc;

    private static final String FROM_TO = "fromTo";
    private static final String BACK_TO = "backTo";

    // flight -> tavel_planner/flightcode=
    @PostMapping("/{flightCode}")
    public ModelAndView postToPlanner(@PathVariable String flightCode, HttpSession sess){
        ModelAndView mav = new ModelAndView();
        System.out.println("post flightcode");

        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("login");
            return mav;
        }
        System.out.println(user);

        //save flight obj to user through flightCode
        FlightInfo flight = fSvc.getFlightObj(flightCode);
        System.out.println(flight);
        fSvc.saveFlightToAcct(flight, user, FROM_TO);
        
        mav.addObject(USER_INFO, user);
        mav.setViewName("redirect:/travel_planner");
        return mav;
    }

    @GetMapping(path={"","/{flightCode}"})
    public ModelAndView getTravelPlanner(HttpSession sess) {
        ModelAndView mav = new ModelAndView();
        System.out.println("get travel planner");
        
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("login");
            return mav;
        }
        System.out.println(user);

        //fetch flight detail
        
        //test input

        mav.addObject(USER_INFO, user);
        mav.setViewName("travel_planner");
        return mav;
    }
    
}
