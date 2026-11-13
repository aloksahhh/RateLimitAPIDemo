package com.example.RateLimitDemoApplication.config;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Bucket4jConfig {



    @Bean(destroyMethod="shutdown")
    public RedisClient redisClient(){
        return RedisClient.create("redis://localhost:6379");
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String,byte[]> redisConnection(RedisClient redisClient){

        return redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

    }

    @Bean
    public ProxyManager<String> proxyManager(StatefulRedisConnection<String,byte[]> redisConnection){

        return Bucket4jLettuce.casBasedBuilder(redisConnection).build();

    }
}
