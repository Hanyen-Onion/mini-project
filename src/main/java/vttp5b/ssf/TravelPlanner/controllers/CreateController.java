package vttp5b.ssf.TravelPlanner.controllers;

import static vttp5b.ssf.TravelPlanner.Util.*;

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
import vttp5b.ssf.TravelPlanner.models.User;
import vttp5b.ssf.TravelPlanner.services.LoginService;
import vttp5b.ssf.TravelPlanner.services.SessionService;

@Controller
@RequestMapping
public class CreateController {

    @Autowired
    private LoginService loginSvc;

    @Autowired
    private SessionService sSvc;

    @PostMapping("create")
    public ModelAndView postCreate(
        @Valid @ModelAttribute("userInfo") User userForm, 
        BindingResult bind, HttpSession sess) {
        
        ModelAndView mav = new ModelAndView();
        User user =  sSvc.getSessionPreLogin(sess);
        
        //validation
        if (bind.hasErrors()) {
            mav.setViewName("create");
            return mav;
        }
        //check pw and username
        if (loginSvc.isCreateSuccessful(userForm, bind) == false) {
            mav.setViewName("create");
            return mav;
        }

        //if session is new and obj is still empty
        if ((user.getUsername() == null) || (user.getPassword()==null)
                ||(user.getUserId() == null)) {
                    
            user.setUsername(userForm.getUsername());
            user.setPassword(userForm.getPassword());
            user.setUserId(User.genId());
            sess.setAttribute(USER_INFO, user);
        }
        loginSvc.saveUser(user);

        mav.addObject(USER_INFO, user);
        mav.addObject("username", user.getUsername());
        mav.addObject("userId", user.getUserId());
        mav.setViewName("login");
        return mav;
    }
    
    @GetMapping("create")
    public ModelAndView getCreate() {

        ModelAndView mav = new ModelAndView();

        mav.addObject(USER_INFO, new User());
        mav.setViewName("create");
        return mav;
    }

}
