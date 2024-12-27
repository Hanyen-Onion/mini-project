package vttp5b.ssf.miniProject1.repositories;

import static vttp5b.ssf.miniProject1.Util.USER_INFO;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import vttp5b.ssf.miniProject1.models.*;

@Repository
public class PlannerRepository {

    @Autowired @Qualifier("redis-string")
    private RedisTemplate<String, String> template;

    private static final String FLIGHT_LIST = "flightList";
    private static final String ADDR_LIST = "addressList";

//tempAddrList


    //smembers key(addrList)
    public Set<String> retrieveTempAddrList() {
        SetOperations<String, String> setOps = template.opsForSet();
        Set<String> redisList = setOps.members(ADDR_LIST);

        return redisList;
    }

    //sadd key(addrtList) (member)DayItinObj, ...
    public void cacheAddrSearchList(List<DayItinerary> list) {
        SetOperations<String, String> setOps = template.opsForSet();
        
        list.forEach(a -> setOps.add(ADDR_LIST, a.toString()));
    }

//flightList

    //smembers key(flightList)
    public Set<String> retrieveFlightList() {
        SetOperations<String, String> setOps = template.opsForSet();
        Set<String> redisList = setOps.members(FLIGHT_LIST);

        return redisList;
    }

    //sadd key(flightList) (member)flightObj, ...
    public void cacheFlights(List<FlightInfo> list) {
        SetOperations<String, String> setOps = template.opsForSet();
        
        list.forEach(f -> setOps.add(FLIGHT_LIST, f.toString()));
        expire(FLIGHT_LIST, 86400);
    }

//general

    //expire                                            
    public void expire(String key, int seconds) {
        template.expire(key,seconds,TimeUnit.SECONDS); //300s (5min) 86,400s(1day)
    }
    
    //exists
    public Boolean isKeyExist(String key) { //check for key exist
        return template.hasKey(key);
    }

    // //hset key(user:username_id) ArrayName(Days)
    // public void saveItineraryToRedis(User userInfo, DayItinerary itinerary) {

    // }

    
    //hexists                               //check for hkey exist 
    public Boolean ishKeyExist(String key, String hkey) {
        return template.opsForHash().hasKey(key, hkey);
    }
    


//user

    //hset key(user:username_id) date(init_list)

    //hget key(user:username_id) flightInfo
    public String getFlight(User user, String hkey) {
        HashOperations<String, String, String> hashOps = template.opsForHash();

        String fString = hashOps.get(User.getUserRedisKey(user), hkey);

        if (ishKeyExist(User.getUserRedisKey(user), hkey)) {
            return fString;
        }
        return null;
    }

    //hset key(user:username_id) flightInfo                     //hashkey name should be fromTo, backTo
    public void saveFlightToRedis(User userInfo, FlightInfo flightInfo, String hkey) {
        template.opsForHash().put(User.getUserRedisKey(userInfo), hkey, flightInfo.toString());
    }

    //keys user:
    public List<User> getUserList() {
        Set<String> users = template.keys("user*");
        List<User> userList = new LinkedList<>();
        
        for (String s: users) {

            User u = getUser(s);
            if (u != null) {
                userList.add(u);
            }
        }
        return userList;
    }

    //hget key(user:username_id) userInfo
    public User getUser(String userKey) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        
        if (isKeyExist(userKey)) {
            String userString = hashOps.get(userKey,USER_INFO);
            User userRedis = User.parseToUserObj(userString);
            
            return userRedis;
        }
        System.out.println("user not exist");
        return null;
    }

    //hset key(user:username_id) userInfo
    public void saveUserToRedis(User user) {
        template.opsForHash().put(User.getUserRedisKey(user), USER_INFO, user.toString());
    }  
}
