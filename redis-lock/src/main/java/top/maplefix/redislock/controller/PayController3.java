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
public class PayController3 {

    @Resource
    private StringRedisTemplate template;
    /**
     * 分布式部署，用nginx作为负载均衡，采用轮询策略
     * 即使是加了synchronized锁，也无法解决商品被重复卖的问题
     * @return String
     */
    @RequestMapping("/buy3")
    public String buy(){
        synchronized (this) {
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
        }
    }
}
