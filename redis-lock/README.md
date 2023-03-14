# Redis实现分布式锁的几种方案
## 背景
日常开发中，秒杀下单付款、抢红包等业务场景都需要用到分布式锁。而Redis非常适合作为分布式锁来使用。本文将从几个方面来分析Redis实现分布式锁的几种方案。
什么是分布式锁

分布式锁其实就是，控制分布式系统不同进程共同访问共享资源的一种锁的实现。如果不同的系统或同一个系统的不同主机之间共享了某个临界资源，往往需要用互斥来防止彼此干扰，保证一致性。

一把靠谱的分布式锁应该具有哪些特征：

- 互斥性：任意时刻，只有一个客户端能持有锁

- 超时释放：持有锁超时时，可以自动释放，防止死锁

- 可重入性：一个线程获取了锁之后可以再次对其请求加锁

- 高可用、高性能：加锁和释放锁需要开销尽可能地，同时也要保证高可用

- 安全性：锁只能被持有的客户端删除，不能被其他客户端删除

## 单机系统数据一致性

在单体应用中，我们对共享数据的加锁一般比较简单，对并发的操作进行加锁，保证对数据的操作具有原子性。
`synchronized`
`ReentrantLock`
可以使用关键字synchronized对方法或者代码块进行隐式加锁，由JVM来实现加锁及释放过程。
也可以用ReentrantLock来显式加锁 lock.lock()，手动释放lock.unlock()。

## Redis分布式锁方案

上面解决了单体应用的数据一致性问题，但如果是分布式架构部署则需要用到分布式锁来解决数据一致性。
### 方案一

使用Redis的set命令来实现分布式锁。

SET KEY VALUE [EX seconds] [PX milliseconds] [NX|XX]

    EX seconds 设置指定的到期时间(以秒为单位)

    PX milliseconds 设置指定的到期时间(以毫秒为单位)

    NX 仅在键不存在时设置键

    XX 只有在键已存在时才设置

举个抢购更新库存的例子，伪代码如下：
```
// Redis分布式锁的key
String REDIS_LOCK = "good_lock";
public String pay(){
// 每个人进来先要进行加锁，key值为"good_lock"，value随机生成
String value = UUID.randomUUID().toString().replace("-","");
try{
    // 加锁
    Boolean flag = redisTemplate.opsForValue().setIfAbsent(REDIS_LOCK, value);
    // 加锁失败
    if(!flag){
        return "抢锁失败！";
    }
    System.out.println( value+ " 抢锁成功");
    String result = template.opsForValue().get("goods:001");
    int total = result == null ? 0 : Integer.parseInt(result);
    if (total > 0) {
        int realTotal = total - 1;
        template.opsForValue().set("goods:001", String.valueOf(realTotal));
        // 如果在抢到锁之后，删除锁之前，发生了异常，锁就无法被释放，
        return "购买商品成功，库存还剩：" + realTotal + "件";
    } else {
        System.out.println("购买商品失败，服务端口为xxx");
    }
        return "购买商品失败，服务端口为xxx";
    }finally {
    // 释放锁
        redisTemplate.delete(REDIS_LOCK);
    }
}
```
### 方案二

方案一可以解决分布式架构中数据一致性问题，但是仔细想想，如果上面的微服务运行期间直接挂了，代码层面根本走不到finally，也就是说在服务宕机前锁没有被释放掉，这样的话就没法保证能释放锁。

所以还需要对key加一个过期时间，Redis中设置过期时间有两种方法
```java
redisTemplate.expire(REDIS_LOCK,10, TimeUnit.SECONDS)
redisTemplate.opsForValue().setIfAbsent(REDIS_LOCK, value,10L,TimeUnit.SECONDS)
```
第一种方法需要单独的一行代码，且并没有与加锁放在同一步操作，所以不具备原子性，也会出问题

第二种方法在加锁的同时就进行了设置过期时间，所有没有问题，这里采用这种方式

方案一中调整一下加锁设置过期时间，其余代码还是不变：

### 方案三

方案二解决了因服务宕机无法释放锁的问题，但是设置过期时间为10秒，仔细想想还有问题。
设置key过期时间为10秒，假设业务逻辑处理比较复杂需要调用其他服务总耗时为15秒，当10秒后key就过期被删除了，当程序15秒后处理请求完成继续执行代码时就会把被人设置的key给删除了。所以，谁上的锁谁自己才能删除！
这样就可以解决因服务耗时太长导致释放了别人的锁的问题。
### 方案四

在方案三下，规定了谁上的锁只有自己才可以删除，但是finally块的判断和del的删除操作不是原子操作，并发的时候也会出问题，并发要保证数据一致性肯定要保证对数据的操作具有原子性。

在Redis的set命令介绍中，推荐用Lua脚本来进行锁的删除

### 方案五

在方案四下，规定了谁上的锁谁才可以删除，并且解决了删除操作原子性的问题。但是没有考虑缓存续命，以及分Redis集群不熟悉爱，异步复制造成锁丢失：主节点还没来得及吧刚刚set进来的数据给从节点就挂了。所以直接上终极大招：RedLock的Redisson实现。

### 什么是缓存续命

Redis分布式锁过期了，但是业务逻辑还没处理完怎么办？Redisson提供了一个看门狗（Watch dog）机制，定期检查（每1/3的锁时间检查1次），如果线程还持有锁，则刷新锁过期时间。

在获取锁成功后，给锁加一个 watchdog，watchdog 会起一个定时任务，在锁没有被释放且快要过期的时候会续期。
## 总结

使用Redis实现分布式锁直接使用方案五即可，Redission提供了强大的锁实现，我们可以直接使用也可以自己稍加封装成基础组件，用来提供分布式锁的能力，用于业务锁、接口幂等性校验等。