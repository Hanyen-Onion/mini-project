package vttp5b.ssf.miniProject1.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vttp5b.ssf.miniProject1.models.AddressSearchParams;
import vttp5b.ssf.miniProject1.models.DayItinerary;
import vttp5b.ssf.miniProject1.models.User;
import vttp5b.ssf.miniProject1.services.CardService;
import vttp5b.ssf.miniProject1.services.SessionService;
import static vttp5b.ssf.miniProject1.Util.*;

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
        @RequestBody String textQuery, @RequestBody String address,
        @PathVariable(required = false) String date, @PathVariable(required = false) String time) {
        ModelAndView mav = new ModelAndView();
        //User user = sSvc.getSessionPostLogin(sess);
        System.out.println("post search address");
        
        //get input from search
        AddressSearchParams params = new AddressSearchParams();
        params.setQuery(textQuery);
        //get list of possible results
        List<DayItinerary> searchResult = cardSvc.getTextSearchApi(params);
        String parsedTime = parseItinTime(date, time);
        searchResult.forEach(itin -> itin.setTime(parsedTime));
   
        //cache to redis
        cardSvc.cacheAddrList(searchResult);
        
        mav.addObject("time", time);
        mav.addObject("date", date);
        mav.addObject("searchResult", searchResult);
        mav.setViewName("search_address");
        return mav;
    }

    //card -> search
    @GetMapping(path={"/{date}/{time}","/{date}"}) 
    public ModelAndView getAddressSearch(@PathVariable(required = false) String date, @PathVariable(required = false) String time, @RequestParam(required = false, value="time") String timeParam) {
        ModelAndView mav = new ModelAndView();
        //User user = sSvc.getSessionPostLogin(sess);
        System.out.println("get search address");
        System.out.println(time);
        System.out.println("param"+ timeParam);
        
        mav.addObject("time", time);
        mav.addObject("time", timeParam);
        mav.addObject("date", date);
        mav.setViewName("search_address");
        return mav;
    }
}
