package com.caston.base_on_spring_boot.redis.controller;

import com.caston.base_on_spring_boot.redis.service.RedisService;
import com.caston.base_on_spring_boot.swagger.entity.Hello;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedisService redisService;
    @Resource
    private RedissonClient redissonClient;

    @GetMapping("/set")
    public void set() {
        redisTemplate.opsForValue().set("key4", new Hello("caston", 28), 1, TimeUnit.MINUTES);
    }

    @GetMapping("/get")
    public Hello get() {
        return (Hello) redisTemplate.opsForValue().get("key3");
    }

    @GetMapping("/setName")
    public Hello setName() {
        return (Hello) redisService.selectByPrimaryKey(17);
    }

    @GetMapping("/getName")
    public Hello getName() {
        return (Hello) redisService.select(17);
    }

    @PostMapping("/send2Redis")
    public void send2Redis(String message) {
        redisTemplate.convertAndSend("caston", message);
    }

    // 商品key
    private static final String KEY = "book";
    // 库存数量
    private static final Long STOCK = 50L;

    @GetMapping("/init")
    public String init() {
        redisTemplate.opsForValue().set(KEY, STOCK);
        return "初始化成功～";
    }

    @ApiOperation("测试添加分布式锁后的超卖现象")
    @GetMapping("/buy")
    public String buy() {
        RLock lock = null;
        try {
            lock = redissonClient.getLock("lock");
            if (lock.tryLock(3, TimeUnit.SECONDS)) {
                RAtomicLong buyBefore = redissonClient.getAtomicLong(KEY);
                if (Objects.isNull(buyBefore)) {
                    System.out.println("未找到" + KEY + "的库存信息~");
                    return "暂未上架～";
                }
                long buyBeforeL = buyBefore.get();
                if (buyBeforeL > 0) {
                    Long buyAfter = buyBefore.decrementAndGet();
                    System.out.println("剩余图书==={" + buyAfter + "}");
                    return "购买成功～";
                } else {
                    System.out.println("库存不足～");
                    return "库存不足～";
                }
            } else {
                System.out.println("获取锁失败～");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //如果当前线程保持锁定则解锁
            if (null != lock && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return "系统错误～";
    }

    @ApiOperation(value = "测试超卖现象", notes = "超卖：指并发下有实际数量已经没有，但是有线程还是通过了判断条件")
    @GetMapping("buy1")
    public String buy1() {
        // 获取到当前库存
        String buyBefore = redisTemplate.opsForValue().get(KEY).toString();
        if (Objects.isNull(buyBefore)) {
            System.out.println("未找到" + KEY + "的库存信息~");
            return "暂未上架～";
        }
        long buyBeforeL = Long.parseLong(buyBefore);
        if (buyBeforeL > 0) {
            // 对库存进行-1操作
            Long buyAfter = redisTemplate.opsForValue().decrement(KEY);
            System.out.println("剩余图书==={" + buyAfter + "}");
            return "购买成功～";
        } else {
            System.out.println("库存不足～");
            return "库存不足～";
        }
    }
}
