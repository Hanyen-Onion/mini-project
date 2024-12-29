package vttp5b.ssf.TravelPlanner.controllers;

import static vttp5b.ssf.TravelPlanner.Util.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import vttp5b.ssf.TravelPlanner.models.*;
import vttp5b.ssf.TravelPlanner.services.*;

@Controller
@RequestMapping("/search_address")
public class AddressController {
    
    @Autowired 
    private CardService cardSvc;

    @Autowired
    private SessionService sSvc;

    // search -> search
    @PostMapping("/{date}/{time}")
    public ModelAndView postAddressSearch(
        @RequestBody MultiValueMap<String, String> form, HttpSession sess,
        @PathVariable(required = false) String date, @PathVariable(required = false) String time) {
        
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null|| mav.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            user = sSvc.getSessionPreLogin(sess);
            mav.setViewName("redirect:/login");
            return mav;
        }
    
        System.out.println("post search address");
        
        //get input from search
        String query = form.getFirst("textQuery");
        SearchParams params = new SearchParams();
        params.setQuery(query);

        //get list of possible results
        List<DayItinerary> searchResult = cardSvc.getTextSearchApi(params);
  
        if (searchResult == null) {
            mav.setStatus(HttpStatusCode.valueOf(400));
            mav.setViewName("redirect:/login");
            return mav;
        }
        String parsedTime = parseTime(date, time);
        searchResult.forEach(itin -> itin.setTime(parsedTime));

        //cache to redis
        cardSvc.cacheAddrList(searchResult);
        
        mav.addObject("time", time);
        mav.addObject("date", date);
        mav.addObject("searchResult", searchResult);
        mav.addObject("user", user);
        mav.setViewName("search_address");
        return mav;
    }

    //card -> search
    @GetMapping(path={"/{date}/{time}", "/{date}{time}"}) 
    public ModelAndView getAddressSearch(
        @PathVariable(required = false, name="date") String date, @PathVariable(required = false, name="time") String pathTime,
        @RequestParam(required =false, name="date") String getDate,
        @RequestParam(required = false, value="time") String timeParam, HttpSession sess) {
        
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null|| mav.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            user = sSvc.getSessionPreLogin(sess);
            mav.setViewName("redirect:/login");
            return mav;
        }
        if (date == null|| getDate == null) {
            mav.setViewName("redirect:/travel_planner");
            return mav;
        }
        
        mav.addObject("time", timeParam);
        mav.addObject("time", pathTime);
        mav.addObject("date", date);
        mav.addObject("date", getDate);
        mav.addObject("user", user);
        mav.setViewName("search_address");
        return mav;
    }


}
