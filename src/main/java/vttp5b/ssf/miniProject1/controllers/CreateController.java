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
@RequestMapping
public class CreateController {

    @Autowired
    private LoginService loginSvc;

    @Autowired
    private SessionService sSvc;

    @PostMapping("create")
    public ModelAndView postCreate(@Valid @ModelAttribute("userInfo") User userForm, BindingResult bind, HttpSession sess) {
        
        ModelAndView mav = new ModelAndView();

        System.out.println("post create");

        //validation
        if (bind.hasErrors()) {
            mav.setViewName("create");
            return mav;
        }
        //check pw
        if (loginSvc.isCreateSuccessful(userForm, bind) == false) {
            
            mav.setViewName("create");
            return mav;
        }
        User user = sSvc.getSession(sess);

        //if session is new and obj is still empty
        if ((user.getUsername() == null)&&(user.getPassword()==null)) {
            user.setUsername(userForm.getUsername());
            user.setPassword(userForm.getPassword());
            user.setUserId(sess.getId());
            sess.setAttribute(USER_INFO, user);
        }
        loginSvc.saveUser(user);
        System.out.println(user);
        
        mav.addObject(USER_INFO, user);
        mav.addObject("username", user.getUsername());
        mav.addObject("userId", user.getUserId());
        mav.setViewName("create_successful");
        return mav;
    }
    
    @GetMapping("create")
    public ModelAndView getCreate() {
        ModelAndView mav = new ModelAndView();
        System.out.println("get create");

        mav.addObject(USER_INFO, new User());
        mav.setViewName("create");
        return mav;
    }

}
