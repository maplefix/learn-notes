package top.maplefix.redislock.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import top.maplefix.redislock.utils.RedisUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author : wangjg
 * @date : 2023/3/14 17:43
 */
@RestController
public class PayController7 {

    public static final String REDIS_LOCK = "good_lock";
    @Resource
    private StringRedisTemplate template;

    /**
     * 在第六种情况下，规定了谁上的锁，谁才能删除
     * 但finally快的判断和del删除操作不是原子操作，并发的时候也会出问题
     * 并发嘛，就是要保证数据的一致性，保证数据的一致性，最好要保证对数据的操作具有原子性
     */
    @RequestMapping("/buy7")
    public String buy(){
        // 每个人进来先要进行加锁，key值为"good_lock"
        String value = UUID.randomUUID().toString().replace("-","");
        try{
            // 为key加一个过期时间
            Boolean flag = template.opsForValue().setIfAbsent(REDIS_LOCK, value,10L, TimeUnit.SECONDS);

            // 加锁失败
            if(Boolean.FALSE.equals(flag)){
                return "抢锁失败！";
            }
            System.out.println( value+ " 抢锁成功");
            String result = template.opsForValue().get("goods:001");
            int total = result == null ? 0 : Integer.parseInt(result);
            if (total > 0) {
                // 如果在此处需要调用其他微服务，处理时间较长。。。
                int realTotal = total - 1;
                template.opsForValue().set("goods:001", String.valueOf(realTotal));
                System.out.println("购买商品成功，库存还剩：" + realTotal + "件， 服务端口为8001");
                return "购买商品成功，库存还剩：" + realTotal + "件， 服务端口为8001";
            } else {
                System.out.println("购买商品失败，服务端口为8001");
            }
            return "购买商品失败，服务端口为8001";
        }finally {
            // 谁加的锁，谁才能删除
            // 也可以使用redis事务
            // https://redis.io/commands/set
            // 使用Lua脚本，进行锁的删除

            try (Jedis jedis = RedisUtils.getJedis()) {

                String script = "if redis.call('get',KEYS[1]) == ARGV[1] " +
                        "then " +
                        "return redis.call('del',KEYS[1]) " +
                        "else " +
                        "   return 0 " +
                        "end";

                Object eval = jedis.eval(script, Collections.singletonList(REDIS_LOCK), Collections.singletonList(value));
                if ("1".equals(eval.toString())) {
                    System.out.println("-----del redis lock ok....");
                } else {
                    System.out.println("-----del redis lock error ....");
                }
            } catch (Exception ignored) {

            }

            // redis事务
            //            while(true){
            //                template.watch(REDIS_LOCK);
            //                if(template.opsForValue().get(REDIS_LOCK).equalsIgnoreCase(value)){
            //                    template.setEnableTransactionSupport(true);
            //                    template.multi();
            //                    template.delete(REDIS_LOCK);
            //                    List<Object> list = template.exec();
            //                    if(list == null){
            //                        continue;
            //                    }
            //                }
            //                template.unwatch();
            //                break;
            //            }
        }
    }
}
