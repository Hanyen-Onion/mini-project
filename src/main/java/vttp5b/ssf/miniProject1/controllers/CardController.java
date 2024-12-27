package vttp5b.ssf.miniProject1.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import vttp5b.ssf.miniProject1.models.DayItinerary;
import vttp5b.ssf.miniProject1.models.User;
import vttp5b.ssf.miniProject1.services.CardService;
import vttp5b.ssf.miniProject1.services.SessionService;

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
        @RequestBody(required = false ) String displayName, @RequestBody(required = false ) String address, 
        @PathVariable(required = false) String date, HttpSession sess) {
        
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }

        System.out.println("post card");
        DayItinerary itin = new DayItinerary();

        if (displayName != null) {
            //get cache search and get obj from it using display name
            itin = cardSvc.findAddress(displayName, address);
            itin.setDate(date);

        } else {
            mav.setStatus(HttpStatusCode.valueOf(500));
            mav.setViewName("login");
            return mav;
        }
        
        cardSvc.saveItinerary(itin, user);
        List<DayItinerary> itinList = cardSvc.getItinListforTheDay(date, user);

        mav.addObject("addressDisplay", displayName);
        mav.addObject("date", date);
        mav.addObject("itinList", itinList);
        mav.addObject("user", user);
        mav.setViewName("card");

        return mav;
    }

    // planner -> card 
    @GetMapping("/{date}/") //card?date=
    public ModelAndView getCardWithDate(@PathVariable(required = false) String date, HttpSession sess) {
        
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("login");
            return mav;
        }

        System.out.println("get card");
        System.out.println("DATE " + date);

        //get itinList for the day from redis
        List<DayItinerary> itinList = cardSvc.getItinListforTheDay(date, user);

        mav.addObject("date", date);
        mav.addObject("itinList", itinList);
        mav.addObject("user", user);
        mav.setViewName("card");
        return mav;
    }

    // planner -> card 
    @GetMapping(path={"", "/{date}"}) //card?date=
    public ModelAndView getCard(@RequestParam(required = false) String date, HttpSession sess) {
        
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPostLogin(sess);
        if (user == null) {
            mav.setViewName("login");
            return mav;
        }

        System.out.println("get card");
        System.out.println("DATE " + date);

        //get itinList for the day from redis
        List<DayItinerary> itinList = cardSvc.getItinListforTheDay(date, user);

        mav.addObject("date", date);
        mav.addObject("itinList", itinList);
        mav.addObject("user", user);
        mav.setViewName("card");
        return mav;
    }

}
