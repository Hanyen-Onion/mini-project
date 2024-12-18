package vttp5b.ssf.miniProject1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import vttp5b.ssf.miniProject1.models.LoginInfo;
import vttp5b.ssf.miniProject1.repositories.PlannerRepository;
import static vttp5b.ssf.miniProject1.Util.*;

@Service
public class PlannerService {
    
    @Autowired
    private PlannerRepository plannerRepo;

    @Value("${login.username}")
    private String loginUsername;

    @Value("${login.password}")
    private String loginPassword;

    //public List<Flight>

    public Boolean isLoginSuccessful(LoginInfo login, BindingResult bind) {
       
        if ((!login.getUsername().equals(loginUsername))||(!login.getPassword().equals(loginPassword))) {
            
            System.out.println("login failed");
            FieldError usernameErr = new FieldError("loginForm", "username", "wrong username");
            bind.addError(usernameErr);

            FieldError pwErr = new FieldError("loginForm", "password", "wrong password");
            bind.addError(pwErr);
            return false;
       }
       return true;
    }
}
