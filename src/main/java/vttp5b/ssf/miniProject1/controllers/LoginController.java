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
import vttp5b.ssf.miniProject1.models.LoginInfo;
import vttp5b.ssf.miniProject1.services.PlannerService;
import static vttp5b.ssf.miniProject1.Util.*;

@Controller
@RequestMapping(path={"/", "/login"})
public class LoginController {
    
    @Autowired
    private PlannerService plannerSvc;

    @PostMapping
    public ModelAndView postLogin(@Valid @ModelAttribute("login") LoginInfo loginForm, BindingResult bind, HttpSession sess) {
        
        ModelAndView mav = new ModelAndView();;

        //validation
        if (plannerSvc.isLoginSuccessful(loginForm ,bind)==false) {
            System.out.println("err");
            mav.setViewName("login");
            return mav;
        }

        LoginInfo login = getSession(sess);
        //if session is new and obj is still empty
        if ((login.getUsername() == null)&&(login.getPassword()==null)) {
            
            login.setUsername(loginForm.getUsername());
            login.setPassword(loginForm.getPassword());
        }

        System.out.println("sess "+login);

        mav.addObject(LOGIN, login);
        mav.setViewName("travel_planner");

        return mav;
    }

    @GetMapping
    public ModelAndView getLogin() {

        ModelAndView mav = new ModelAndView();

        mav.addObject(LOGIN, new LoginInfo());
        mav.setViewName("login");

        return mav;
    }
}
