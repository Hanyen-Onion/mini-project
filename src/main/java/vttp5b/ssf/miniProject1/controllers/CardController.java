package vttp5b.ssf.miniProject1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import vttp5b.ssf.miniProject1.services.CardService;

@Controller
@RequestMapping("/card")
public class CardController {
    
    @Autowired 
    private CardService cardSvc;

    //search address -> card
    @PostMapping()
    public ModelAndView postAddress(@RequestBody String obj) {
        ModelAndView mav = new ModelAndView();

        System.out.println("post card");
        System.out.println(obj);
        mav.addObject("testObj", obj);
        mav.setViewName("card");

        return mav;
    }

    @GetMapping()
    public ModelAndView getCard() {
        ModelAndView mav = new ModelAndView();

        System.out.println("get card");

        mav.setViewName("card");

        return mav;
    }

}
