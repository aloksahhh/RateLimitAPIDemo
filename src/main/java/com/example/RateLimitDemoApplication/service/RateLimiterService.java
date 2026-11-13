package com.example.RateLimitDemoApplication.service;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {
    private final ProxyManager<String> proxyManager;

    public RateLimiterService(ProxyManager<String> proxyManager){
        this.proxyManager=proxyManager;
    }

    private Bucket getBucket(String key, long capacity){
        BucketConfiguration configuration=BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(capacity)
                        .refillGreedy(capacity, Duration.ofMinutes(1)))
                .build();

        return proxyManager.getProxy(key,()-> configuration);
    }

    public boolean isAllowed(String userId,String ipAddress){
        Bucket userBucket = getBucket("rate-limit:user:"+userId,10);

        Bucket ipBucket = getBucket("rate-limit:ip:"+ipAddress,10);

        if(!userBucket.tryConsume(1)){
            return false;
        }

        if(!ipBucket.tryConsume(1)){
            return false;
        }
        return true;
    }

}
