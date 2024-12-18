package vttp5b.ssf.miniProject1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import vttp5b.ssf.miniProject1.services.PlannerService;

@Controller
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    private PlannerService plannerSvc;

    @GetMapping
    public ModelAndView getFlight() {

        ModelAndView mav = new ModelAndView();

        mav.setViewName("flight");

        return mav;
    }
    
}
