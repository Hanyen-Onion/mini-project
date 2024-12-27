package vttp5b.ssf.miniProject1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vttp5b.ssf.miniProject1.services.CardService;

@Controller
@RequestMapping()
public class TestController {
    
    @Autowired 
    private CardService cardSvc;

    @PostMapping(path={"/test2/save/{date}"})
    public ModelAndView post2Save(@PathVariable String date) {
        ModelAndView mav = new ModelAndView();

        System.out.println("post test2");

        mav.addObject("date", date);
        mav.setViewName("save");

        return mav;
    }

    @PostMapping(path={"/test2/{date}"})
    public ModelAndView postTest2(@PathVariable String date) {
        ModelAndView mav = new ModelAndView();

        System.out.println("post test2");

        mav.addObject("date", date);
        mav.setViewName("test2");

        return mav;
    }

    @GetMapping(path={"/test"})
    public ModelAndView getTest() {
        ModelAndView mav = new ModelAndView();

        System.out.println("get test");

        mav.setViewName("test");

        return mav;
    }

    @GetMapping(path={"/test2/{date}", "/test2"})
    public ModelAndView getTest2(@RequestParam(required = false) String date) {
        ModelAndView mav = new ModelAndView();
        System.out.println(date);

        System.out.println("get test2");
        mav.addObject("date", date);
        mav.setViewName("test2");

        return mav;
    }

    @GetMapping(path={"/test2/save/{date}"})
    public ModelAndView getTest2Save(@PathVariable(required = false) String date) {
        ModelAndView mav = new ModelAndView();
        System.out.println(date);

        System.out.println("get save");
        mav.addObject("date", date);
        mav.setViewName("save");

        return mav;
    }

}
