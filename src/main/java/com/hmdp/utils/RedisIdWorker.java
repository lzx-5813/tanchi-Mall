package com.hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

//@Component
//public class RedisIdWorker {
//
//    @Resource
//    private StringRedisTemplate stringRedisTemplate;
//
//    /**
//     * 开始时间戳
//     */
//    private static final long BEGIN_TIMESTAMP = 1640995200L;
//    private static final int COUNT_BITS = 32;
//    public Long nextId(String keyPrefix){
//        //1.生成时间戳
//        LocalDateTime now = LocalDateTime.now();
//        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
//        Long timestamp = nowSecond-BEGIN_TIMESTAMP;
//
//        //2.生成序列号
//        //2.1获取当前日期，精确到天
//        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
//        //2.2自增长
//        Long count = stringRedisTemplate.opsForValue().increment("icr" + keyPrefix + ":"+date);
//
//
//        //3.拼接并返回
//        return timestamp >> COUNT_BITS | count;
//    }
//
//
//}
/**
 * 秒杀券 订单唯一ID：时间戳 + 序列号
 */
@Component
public class RedisIdWorker {

    private static final long BEGIN_TIMESTAMP = 1640995200L;       //开始时间戳
    private static final long COUNT_BITS = 32;      //序列号位数

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public long nextId(String keyPrefix) {
        //1.时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        //2.序列号
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));

        long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);
        //拼接生成
        return timestamp << COUNT_BITS | count;
    }

}
