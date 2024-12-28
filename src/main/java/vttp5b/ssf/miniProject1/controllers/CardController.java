package vttp5b.ssf.miniProject1.controllers;

import static vttp5b.ssf.miniProject1.Util.parseBackTime;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import vttp5b.ssf.miniProject1.models.*;
import vttp5b.ssf.miniProject1.services.*;

@Controller
@RequestMapping("/card")
public class CardController {
    
    @Autowired 
    private CardService cardSvc;

    @Autowired 
    private SessionService sSvc;

    //search address -> card
    @PostMapping("/{date}")
    public ModelAndView postAddress(
        @RequestBody(required = false ) MultiValueMap<String, String> form, 
        @PathVariable(required = false) String date, HttpSession sess) {
        
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        } else if (date == null) {
            mav.setStatus(HttpStatusCode.valueOf(500));
            mav.setViewName("redirect:/travel_planner");
            return mav;
        }

        String displayName = form.getFirst("addrDisplay");
        String address = form.getFirst("addr");
        String time = form.getFirst("time");

        DayItinerary itin = new DayItinerary();
        //get cache search and get obj from it using display name
        DayItinerary cachedResult = cardSvc.findAddress(displayName, address);
        itin.setDate(date);
        itin.setAddress(address);
        if (displayName != null) {
            itin.setDisplayName(cachedResult.getDisplayName());
        }
        itin.setDisplayName(address);
        itin.setEmbedMapUrl(cachedResult.getEmbedMapUrl());
        itin.setPlaceId(cachedResult.getPlaceId());
        itin.setTime(time);

        cardSvc.saveItinerary(itin, user);
        List<DayItinerary> dateList = cardSvc.getItinListforTheDay(date, user);

        mav.addObject("dateList", dateList);
        mav.addObject("user", user);
        mav.addObject("date", date);
        mav.addObject("time", parseBackTime(time));
        mav.setViewName("card");

        return mav;
    }

    // planner -> card 
    @GetMapping(path={"", "/{date}"}) //card?date=
    public ModelAndView getCard(@RequestParam(required = false) String date, HttpSession sess) {
        
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null|| mav.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            user = sSvc.getSessionPreLogin(sess);
            mav.setViewName("redirect:/login");
            return mav;
        }

        //get itinList for the day from redis
        List<DayItinerary> dateList = cardSvc.getItinListforTheDay(date, user);

        mav.addObject("date", date);
        mav.addObject("dateList", dateList);
        mav.addObject("user", user);
        mav.setViewName("card");
        return mav;
    }

}
