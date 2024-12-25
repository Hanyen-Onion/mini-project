package vttp5b.ssf.miniProject1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import vttp5b.ssf.miniProject1.services.CardService;

@Controller
@RequestMapping("/search_address")
public class AddressController {
    
    @Autowired 
    private CardService cardSvc;

    @GetMapping
    public ModelAndView getAddressSearch() {
        ModelAndView mav = new ModelAndView();
        
        System.out.println("get search address");
        mav.setViewName("/search_address");

        return mav;
    }
}
