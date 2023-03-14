package top.maplefix.redislock.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : wangjg
 * @date : 2023/3/14 17:43
 */
@RestController
public class PayController2 {

    @Resource
    private StringRedisTemplate template;
    Lock lock = new ReentrantLock();
    /**
     * 单体应用的情况下，对并发的操作加锁，保证对数据的操作具有原子性
     * @return String
     */
    @RequestMapping("/buy2")
    public String buy(){
        lock.lock();
        try {
            String prefix = "goods:001";
            String result = template.opsForValue().get(prefix);
            int total = result == null ? 0 : Integer.parseInt(result);
            if(total > 0){
                int realTotal = total -1;
                template.opsForValue().set(prefix,String.valueOf(realTotal));
                return "购买成功，库存还剩" + realTotal + "件";
            }else {
                return "购买失败，库存为" + result;
            }
        }catch (Exception e){
            lock.unlock();
        }finally {
            lock.unlock();
        }
        return "购买失败";
    }
}
