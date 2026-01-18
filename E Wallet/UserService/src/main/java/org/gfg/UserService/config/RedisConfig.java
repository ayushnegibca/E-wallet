package org.gfg.UserService.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.gfg.UserService.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
public class RedisConfig {


    @Bean
    public RedisTemplate<String,String> otpRedisTemplate(){
        RedisTemplate<String,String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return  redisTemplate;
    }


    @Bean
    public RedisTemplate<String, String> userRedisTemplate(){
        RedisTemplate<String,String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory();
        lettuceConnectionFactory.setPassword("dizVUaLCCzDMlhB3pxiA4YOMsFbokIwA");
        lettuceConnectionFactory.setHostName("redis-18618.crce217.ap-south-1-1.ec2.cloud.redislabs.com");
        lettuceConnectionFactory.setPort(18618);
        lettuceConnectionFactory.start();
        return lettuceConnectionFactory;
    }
}
