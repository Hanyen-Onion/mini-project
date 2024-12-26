package vttp5b.ssf.miniProject1.controllers;

import static vttp5b.ssf.miniProject1.Util.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import vttp5b.ssf.miniProject1.models.FlightInfo;
import vttp5b.ssf.miniProject1.models.FlightSearchParams;
import vttp5b.ssf.miniProject1.models.User;
import vttp5b.ssf.miniProject1.services.FlightService;
import vttp5b.ssf.miniProject1.services.SessionService;

@Controller
@RequestMapping()
public class FlightController {

    @Autowired
    private FlightService fSvc;

    @Autowired
    private SessionService sSvc;

    // flight -> flight , returns filter
    @PostMapping("depart")
    public ModelAndView postSearch(@RequestBody MultiValueMap<String, String> searchParams, @ModelAttribute FlightInfo flightInfo, HttpSession sess) {
        ModelAndView mav = new ModelAndView();
        System.out.println("post search");

        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }
        System.out.println(user);

        FlightSearchParams params = new FlightSearchParams(
            searchParams.getFirst("searchDepAirport"),
            searchParams.getFirst("searchArrAirport"),
            searchParams.getFirst("searchFlightCode"),
            searchParams.getFirst("searchFlightDate"));

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
        System.out.println("get flight");

        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }
        System.out.println(user);

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
        System.out.println("get flight");

        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }
        System.out.println(user);

        List<FlightInfo> allFlightInfoList = fSvc.getFlightList();
        
        mav.addObject("flightInfoList", allFlightInfoList);
        mav.addObject(USER_INFO, user);
        mav.setViewName("depart");

        return mav;
    }
    
}
