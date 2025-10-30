package com.ifreeshare.redis;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * redisson 操作类
 */
@Component("redissonService")
public class RedissonService<T> {

    @Autowired
    RedissonClient redissonClient;

    public  RBucket<T> getBucket(String key){
        return redissonClient.getBucket(key);
    }
}
