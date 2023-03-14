package top.maplefix.redislock.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author : wangjg
 * @date : 2023/3/14 17:39
 */
public class RedisUtils {
    private static final JedisPool jedisPool;
    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        jedisPool = new JedisPool(config,"localhost",6379);
    }

    public static Jedis getJedis() throws Exception {
        if(null != jedisPool){
            return jedisPool.getResource();
        }
        throw new Exception("JedisPool is bad!");
    }
}
