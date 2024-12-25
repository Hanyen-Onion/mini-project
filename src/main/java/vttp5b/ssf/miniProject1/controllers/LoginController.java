package vttp5b.ssf.miniProject1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp5b.ssf.miniProject1.models.User;
import vttp5b.ssf.miniProject1.services.LoginService;
import vttp5b.ssf.miniProject1.services.SessionService;

import static vttp5b.ssf.miniProject1.Util.*;

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
        User user = sSvc.getSession(sess);

        System.out.println("post login");

        //validation
        if (bind.hasErrors()) {
            System.out.println("err");
            mav.setViewName("login");
            return mav;
        }

        if (loginSvc.isLoginSuccessful(loginForm ,bind)==false) {
            System.out.println("err");
            mav.setViewName("login");
            return mav;
        }

        // if session is new and obj is still empty
        if ((user.getUsername() == null)&&(user.getPassword()==null)) {
            //find user info from redis and populate sess
            user = sSvc.populateLoginSess(loginForm);
        }

        System.out.println(user);

        mav.addObject(USER_INFO, user);
        mav.setViewName("travel_planner");

        return mav;
    }
    

    @GetMapping
    public ModelAndView getLogin(HttpSession sess) {

        ModelAndView mav = new ModelAndView();
        User user = sSvc.getSession(sess);

        System.out.println("get login");

        mav.addObject(USER_INFO, user);
        mav.setViewName("login");

        return mav;
    }
}
