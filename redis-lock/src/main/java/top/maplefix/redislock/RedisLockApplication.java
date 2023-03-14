package top.maplefix.redislock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 使用spring-integration，用于springboot集成redis实现分布式锁
 * @author : wangjg
 * @date : 2023/3/14 15:24
 */
@SpringBootApplication
public class RedisLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisLockApplication.class, args);
    }

}
