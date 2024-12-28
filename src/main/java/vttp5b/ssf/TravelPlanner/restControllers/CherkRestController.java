package vttp5b.ssf.TravelPlanner.restControllers;

import static vttp5b.ssf.TravelPlanner.Util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import vttp5b.ssf.TravelPlanner.models.User;
import vttp5b.ssf.TravelPlanner.services.SessionService;

@RestController
@RequestMapping 
public class CherkRestController {

    @Autowired
    private SessionService sSvc;

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
    
    @GetMapping(path = {"/search_address/{date}/","card//"})
    public ResponseEntity<String> wrongPath(HttpSession sess) {
        @SuppressWarnings("unused")
        User user = sSvc.getSessionPreLogin(sess);

        // Redirect response
        return ResponseEntity.status(HttpStatus.FOUND) // HTTP 302 redirect status
                            .header(HttpHeaders.LOCATION, "/login") 
                            .build();
    }
}
