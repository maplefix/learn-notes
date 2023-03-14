package top.maplefix.redislock.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : wangjg
 * @date : 2023/3/14 17:43
 */
@RestController
public class PayController1 {

    @Resource
    private StringRedisTemplate template;

    /**
     * 最简单的情况，没有任何并发考虑，即使是单体应用，并发情况相爱数据一致性都有问题
     * @return String
     */
    @RequestMapping("/buy1")
    public String buy(){
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
