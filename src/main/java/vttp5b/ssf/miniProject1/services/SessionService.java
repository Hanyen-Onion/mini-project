package vttp5b.ssf.miniProject1.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vttp5b.ssf.miniProject1.models.User;
import vttp5b.ssf.miniProject1.repositories.PlannerRepository;
import static vttp5b.ssf.miniProject1.Util.*;

@Service
public class SessionService {
    
    @Autowired
    private PlannerRepository pRepo;

    //if loginIsSucessful pull data from redis to put in sess
    public User getUserWithLogin(User loginForm) {
        List<User> userList = pRepo.getUserList();

        if (loginForm.getUsername() != null) {
            Optional<User> foundUserOpt = userList.stream()
            .filter(existingUser -> existingUser.getUsername().equals(loginForm.getUsername()))
            .findFirst();

            if (foundUserOpt.isEmpty()) {
                System.out.println("no user found");
                return null;
            }

            User foundUser = foundUserOpt.get();
            System.out.println("if form got user name, what i get from optional " + foundUser);
            return foundUser;
        }
        
        //find through id
        if (loginForm.getUserId() != null) {
            Optional<User> foundUserOpt = userList.stream()
            .filter(existingUser -> existingUser.getUserId().equals(loginForm.getUserId()))
            .findFirst();

            User foundUser = foundUserOpt.get();
            return foundUser;
        }
        System.out.println("didn't find anything in redis");
        return null;
    }

    public  User getSession(HttpSession sess) {
        User user = (User)sess.getAttribute(USER_INFO);

        //create empty login object if no session
        if (user == null) {
            user = new User();
            sess.setAttribute(USER_INFO, user);
        }
    return user;
    }
}
