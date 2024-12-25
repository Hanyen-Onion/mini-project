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
import vttp5b.ssf.miniProject1.services.PlannerService;

@Controller
@RequestMapping("/travel_planner")
public class PlannerController {

    @Autowired
    private PlannerService plannerSvc;

    @Autowired
    private FlightService fSvc;

    // flight -> tavel_planner/flightcode=
    @PostMapping("/{flightCode}")
    public ModelAndView postToPlanner(@PathVariable String flightCode){
        ModelAndView mav = new ModelAndView();

        //save flight obj to user through flightCode
        FlightInfo flight = fSvc.getFlightObj(flightCode);
        System.out.println(flight);
        
      
        mav.setViewName("travel_planner");
        return mav;
    }

    @GetMapping(path={"","/{flightCode}"})
    public ModelAndView getTravelPlanner(HttpSession sess) {
        ModelAndView mav = new ModelAndView();
        User user = getSession(sess);

        //fetch flight detail
        //should fetch from redis instead of creating new
        FlightInfo testObj = new FlightInfo();

        //test input

        mav.addObject(USER_INFO, user);
        mav.setViewName("travel_planner");
        return mav;
    }
    
}
