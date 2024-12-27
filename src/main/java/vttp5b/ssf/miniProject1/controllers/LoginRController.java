package vttp5b.ssf.miniProject1.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import vttp5b.ssf.miniProject1.models.User;

import static vttp5b.ssf.miniProject1.Util.*;

@RestController
@RequestMapping 
public class LoginRController {
    @GetMapping("/session") 
    public ResponseEntity<String> getSessionRunning(HttpSession sess) {
        
        User userSession = (User)sess.getAttribute(USER_INFO);

        // If the session attribute "USER_INFO" exists, the session is active
        if (userSession != null) {
            return ResponseEntity.ok("Session is running");
        } else {
            return ResponseEntity.status(401).body("Session is not active");
        }
        
    }

}
