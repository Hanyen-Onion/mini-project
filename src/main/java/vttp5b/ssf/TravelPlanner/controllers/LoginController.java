package vttp5b.ssf.TravelPlanner.controllers;

import static vttp5b.ssf.TravelPlanner.Util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp5b.ssf.TravelPlanner.models.User;
import vttp5b.ssf.TravelPlanner.services.*;

@Controller
@RequestMapping(path={"/", "/login"})
public class LoginController {
    
    @Autowired
    private LoginService loginSvc;

    @Autowired
    private SessionService sSvc;

    @PostMapping
    public ModelAndView postLogin(@Valid @ModelAttribute("userInfo") User loginForm, BindingResult bind, HttpSession sess) {
        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPreLogin(sess);

        //validation
        if (bind.hasErrors()) {
            System.out.println("err");
            mav.setViewName("login");
            return mav;
        }

        if (loginSvc.isLoginSuccessful(loginForm ,bind)==false) {
            System.out.println("login err");
            mav.setViewName("login");
            return mav;
        }
        // if session is new and obj is still empty
        if ((user.getUsername() == null)|| (user.getPassword()==null)
                ||(user.getUserId()== null) ) {
            //find user info from redis and populate sess
            User rUser = sSvc.getUserWithLogin(loginForm);
            user.setUsername(rUser.getUsername());
            user.setPassword(rUser.getPassword());
            user.setUserId(rUser.getUserId());
            sess.setAttribute(USER_INFO, user);
        }

        mav.addObject(USER_INFO, user);
        mav.setViewName("redirect:/travel_planner");

        return mav;
    }

    @GetMapping
    public ModelAndView getLogin(HttpSession sess) {

        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSessionPreLogin(sess);

        mav.addObject(USER_INFO, user);
        mav.setViewName("login");

        return mav;
    }
}
