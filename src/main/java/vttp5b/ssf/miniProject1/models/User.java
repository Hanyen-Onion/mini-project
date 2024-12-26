package vttp5b.ssf.miniProject1.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class User {

    @NotEmpty(message = "cannot be empty")
    @Size( min = 2, max = 16, message = "min 2 and max 16 characters")
    private String username;

    @NotEmpty(message = "cannot be empty")
    @Size( min = 2, max = 16, message = "min 2 and max 16 characters")
    private String password;

    private String repeatPassword;
    private String userId;

    //getter setter
    public String getUsername() {    return username;}
    public void setUsername(String username) {    this.username = username;}

    public String getPassword() {    return password;}
    public void setPassword(String password) {    this.password = password;}

    public String getUserId() {    return userId;}
    public void setUserId(String userId) {    this.userId = userId;}

    public String getRepeatPassword() {    return repeatPassword;}
    public void setRepeatPassword(String repeatPassword) {    this.repeatPassword = repeatPassword;}

    public static User parseToUserObj(String string) {
        User user = new User();

        if (string == null) {
            System.out.println("string is null");
            return null;
        }

        String[] fields = string.split("&");
        String[] kv;
        
        for(int i = 0; i < fields.length; i++) {
            kv = fields[i].split("=");
   
            if (kv.length == 2) 
            switch (kv[0]) {
                case "username":
                    user.setUsername(kv[1]);
                    break;
                case "password":
                    user.setPassword(kv[1]);
                    break;
                case "userId":
                    user.setUserId(kv[1]);
                    break;
                default:
                    System.out.println("error with user parsing string");
                    break;
            }
        }
        return user;
    }

    public static String getUserRedisKey(User user) {
        return "user:"+ user.getUsername() + "_" + user.getUserId();
    }

    @Override
    public String toString() {
        return "username=" + username + "&password=" + password +"&userId=" + userId;
    }
}
