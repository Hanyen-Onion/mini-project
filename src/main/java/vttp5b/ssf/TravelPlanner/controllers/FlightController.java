package vttp5b.ssf.TravelPlanner.controllers;

import static vttp5b.ssf.TravelPlanner.Util.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import vttp5b.ssf.TravelPlanner.models.*;
import vttp5b.ssf.TravelPlanner.services.FlightService;
import vttp5b.ssf.TravelPlanner.services.SessionService;

@Controller
@RequestMapping()
public class FlightController {

    @Autowired
    private FlightService fSvc;

    @Autowired
    private SessionService sSvc;

    // flight -> flight , returns filter
    @PostMapping("depart")
    public ModelAndView postSearch(
        @RequestBody MultiValueMap<String, String> searchParams, 
        @ModelAttribute FlightInfo flightInfo, HttpSession sess) {
        
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }

        SearchParams params = new SearchParams();
        params.setArrAirport(searchParams.getFirst("searchArrAirport"));
        params.setDepAirport(searchParams.getFirst("searchDepAirport"));
        params.setFlightCode(searchParams.getFirst("searchFlightCode"));
        params.setDate(searchParams.getFirst("searchFlightDate"));

        List<FlightInfo>  allFlightInfoList = fSvc.getFlightList();
        
        mav.addObject("flightInfoList", fSvc.filter(allFlightInfoList, params));
        mav.addObject(USER_INFO, user);
        mav.setViewName("depart");

        return mav;
    }

    // //get unflitered list
    @GetMapping("/return")
    public ModelAndView getReturn(HttpSession sess) {
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }

        List<FlightInfo> allFlightInfoList = fSvc.getFlightList();
        
        mav.addObject("flightInfoList", allFlightInfoList);
        mav.addObject(USER_INFO, user);
        mav.setViewName("return");

        return mav;
    }

    //get unflitered list
    @GetMapping("/depart")
    public ModelAndView getDepart(HttpSession sess) {
       
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }
        List<FlightInfo> allFlightInfoList = fSvc.getFlightList();
        
        mav.addObject("flightInfoList", allFlightInfoList);
        mav.addObject(USER_INFO, user);
        mav.setViewName("depart");

        return mav;
    }
    
}
