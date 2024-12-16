package vttp5b.ssf.miniProject1.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PlannerRepository {

    @Autowired @Qualifier("redis-object")
    private RedisTemplate<String, Object> template;
    
}
