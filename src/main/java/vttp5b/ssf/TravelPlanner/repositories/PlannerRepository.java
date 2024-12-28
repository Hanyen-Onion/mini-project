package vttp5b.ssf.TravelPlanner.repositories;

import static vttp5b.ssf.TravelPlanner.Util.USER_INFO;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import vttp5b.ssf.TravelPlanner.models.*;

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

//dateSet

    //smembers key(d:name_date))
    public Set<String> retrieveDateSet(String date, String username) {
        SetOperations<String, String> setOps = template.opsForSet();
        
        if (DayItinerary.itinListKey(date) == null||username == null) {
            return null;
        }
        Set<String> redisSet = setOps.members(DayItinerary.itinListKey(date));

        return redisSet;
    }

    //sadd key(d:date) itinObj, itinObj
    public void saveToDateSet(DayItinerary itin, String username) {
        SetOperations<String, String> setOps = template.opsForSet();
        if (itin != null) {
            setOps.add(DayItinerary.itinListKey(itin.getDate()), itin.toString());
        }
    }

//user

    //hdel key(user:username_id) date
    public void delhKey(User user, String date) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        hashOps.delete(User.getUserRedisKey(user), DayItinerary.itinListKey(date));
    }

    //hkeys  key(user:username_id)
    public Set<String> getAllhKeys(User user) {
        HashOperations<String, String, String> hashOps = template.opsForHash();

        Set<String> hkeys = hashOps.keys(User.getUserRedisKey(user));

        if (user == null || !isKeyExist(User.getUserRedisKey(user))) {
            return null;
        }
        return hkeys;
    }

    //hset key(user:username_id) d:date dateSet
    public void saveDateToAcct(DayItinerary itin, User user) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        Set<String> itinSet = retrieveDateSet(itin.getDate(), user.getUsername());
        StringBuilder sb = new StringBuilder();
        
        itinSet.forEach(obj -> {
            //get every Obj to add up to one long string separated by commas
            sb.append(obj).append(",");
        });

        if (sb.length()> 0) 
                sb.setLength(sb.length() -1);

        String itinList = sb.toString();

        hashOps.put(User.getUserRedisKey(user), DayItinerary.itinListKey(itin.getDate()), itinList);
    }

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
        System.out.println("user does not exist");
        return null;
    }

    //hset key(user:username_id) userInfo
    public void saveUserToRedis(User user) {
        template.opsForHash().put(User.getUserRedisKey(user), USER_INFO, user.toString());
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
    
    //hexists                               //check for hkey exist 
    public Boolean ishKeyExist(String key, String hkey) {
        return template.opsForHash().hasKey(key, hkey);
    }
}
