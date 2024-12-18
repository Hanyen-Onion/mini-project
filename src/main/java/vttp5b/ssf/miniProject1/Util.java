package vttp5b.ssf.miniProject1;


import jakarta.servlet.http.HttpSession;
import vttp5b.ssf.miniProject1.models.LoginInfo;

//@Component
public class Util {


    public static final String LOGIN = "login";

    public static LoginInfo getSession(HttpSession sess) {

        LoginInfo login = (LoginInfo)sess.getAttribute(LOGIN);

        //create empty login object if no session
        if (login == null) {
            login = new LoginInfo();
            sess.setAttribute(LOGIN, login);
        }
        
        return login;
    }
}
