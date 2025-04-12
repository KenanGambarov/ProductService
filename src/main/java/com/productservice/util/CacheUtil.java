package com.productservice.util;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CacheUtil {

    private final RedissonClient redissonClient;

    public <T> T getBucket(String cacheKey){
        RBucket<T> bucket = redissonClient.getBucket(cacheKey);
        return bucket == null ? null : bucket.get();
    }

    public <T> void saveToCache(String key, T value, Duration duration){
        var bucket = redissonClient.getBucket(key);
        bucket.set(value);
        bucket.expire(duration);
    }

    public <T> T getOrLoad(String key,Supplier<T> dbLoader, Duration duration) {
        T cached = getBucket(key);
        if (cached != null) return cached;

        T data = dbLoader.get();
        saveToCache(key, data, duration);
        return data;
    }

    public void deleteFromCache(String key){
        redissonClient.getBucket(key).delete();
    }

}
