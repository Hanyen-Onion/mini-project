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
import org.springframework.web.servlet.ModelAndView;

import vttp5b.ssf.miniProject1.models.AddressSearchParams;
import vttp5b.ssf.miniProject1.models.DayItinerary;
import vttp5b.ssf.miniProject1.models.User;
import vttp5b.ssf.miniProject1.services.CardService;
import vttp5b.ssf.miniProject1.services.SessionService;

@Controller
@RequestMapping("/search_address")
public class AddressController {
    
    @Autowired 
    private CardService cardSvc;

    @Autowired
    private SessionService sSvc;

    @PostMapping("/{date}")
    public ModelAndView postAddressSearch(@RequestBody MultiValueMap<String, String> form, @PathVariable(required = false) String date) {
        ModelAndView mav = new ModelAndView();
        //User user = sSvc.getSessionPostLogin(sess);
        System.out.println("post search address");
        
        //get input from search
        AddressSearchParams params = new AddressSearchParams(
            form.getFirst("textQuery"), 
            form.getFirst("time"));
        
        //get list of possible results
        List<DayItinerary> searchResult = cardSvc.getTextSearchApi(params);
        //save to redis

        mav.addObject("time", params.time());
        mav.addObject("date", date);
        mav.addObject("searchResult", searchResult);
        mav.setViewName("redirect:/search_address/{date}");
        return mav;
    }

    @GetMapping("/{date}")
    public ModelAndView getAddressSearch(@PathVariable(required = false) String date) {
        ModelAndView mav = new ModelAndView();
        //User user = sSvc.getSessionPostLogin(sess);
        System.out.println("get search address");
        
        mav.addObject("date", date);
        mav.setViewName("search_address");
        return mav;
    }
}
