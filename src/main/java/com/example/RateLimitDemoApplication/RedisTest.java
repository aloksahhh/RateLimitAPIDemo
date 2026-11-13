package com.example.RateLimitDemoApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisTest implements CommandLineRunner {

    private final StringRedisTemplate redisTemplate;


    public RedisTest(StringRedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }


    @Override
    public void run(String... args) throws Exception {

        redisTemplate.opsForValue().set("rate-limit:test","Redis Connected!");

        String result = redisTemplate.opsForValue().get("rate-limit:test");

        System.out.println("Redis Test: "+ result);

    }
}
