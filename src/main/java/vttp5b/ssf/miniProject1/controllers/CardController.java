package vttp5b.ssf.miniProject1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
        @RequestBody(required = false ) String displayName, 
        @PathVariable(required = false) String date, HttpSession sess) {
        
        ModelAndView mav = new ModelAndView();
        //User user = sSvc.getSessionPostLogin(sess);

        System.out.println("post card");

        //get cache search and get obj from it using display name
        DayItinerary itin = cardSvc.findAddress(displayName);
        itin.setDate(date);

        //save to redis acct
        

        mav.addObject("addressDisplay", displayName);
        mav.addObject("date", date);
        mav.addObject("itinerary", itin);
        mav.setViewName("card");

        return mav;
    }

    // planner -> card 
    @GetMapping(path={""}) //card?date=
    public ModelAndView getCard(@RequestParam(required = false) String date) {
        ModelAndView mav = new ModelAndView();

        System.out.println("get card");
        System.out.println("DATE " + date);

        mav.addObject("date", date);
        mav.setViewName("card");
        return mav;
    }

}
