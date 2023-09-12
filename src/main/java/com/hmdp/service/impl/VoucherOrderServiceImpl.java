package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pony
 */
@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

//    @PostConstruct
//    private void init(){
//        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
//    }
//    private class VoucherOrderHandler implements Runnable{
//        String queueName = "stream.orders";
//        @Override
//        public void run() {
//            while (true){
//                try {
//                    //1.获取队列中的订单信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS streams.order >
//                    List<MapRecord<String,Object,Object>> list =  stringRedisTemplate.opsForStream().read(
//                            Consumer.from("g1","c1"),
//                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
//                            StreamOffset.create(queueName, ReadOffset.lastConsumed())
//                    );
//                    //2.判断消息是否获取成功
//                    if(list == null || list.isEmpty()){
//                        //2.1 如果消息获取失败，说明没有消息，继续下一次循环
//                        continue;
//                    }
//                    //3.解析消息中的订单信息
//                    MapRecord<String, Object, Object> record = list.get(0);
//                    Map<Object, Object> values = record.getValue();
//                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), true);
//                    //3.如果获取成功，可以下单
//                    handleVoucherOrder(voucherOrder);
//                    //4.ACK确认 SACK stream.orders g1 id
//                    stringRedisTemplate.opsForStream().acknowledge(queueName,"g1",record.getId());
//                } catch (Exception e) {
//                    log.error("订单处理异常",e);
//                    handlePendingList();
//                }
//            }
//        }

//        private void handlePendingList() {
//            while (true){
//                try {
//                    //1.获取pending-list中的订单信息 XREADGROUP GROUP g1 c1 COUNT 1  STREAMS streams.order 0
//                    List<MapRecord<String,Object,Object>> list =  stringRedisTemplate.opsForStream().read(
//                            Consumer.from("g1","c1"),
//                            StreamReadOptions.empty().count(1),
//                            StreamOffset.create(queueName, ReadOffset.from("0"))
//                    );
//                    //2.判断消息是否获取成功
//                    if(list == null || list.isEmpty()){
//                        //2.1 如果消息获取失败，说明pending-list没有异常消息
//                        break;
//                    }
//                    //3.解析消息中的订单信息
//                    MapRecord<String, Object, Object> record = list.get(0);
//                    Map<Object, Object> values = record.getValue();
//                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), true);
//                    //3.如果获取成功，可以下单
//                    handleVoucherOrder(voucherOrder);
//                    //4.ACK确认 SACK stream.orders g1 id
//                    stringRedisTemplate.opsForStream().acknowledge(queueName,"g1",record.getId());
//                } catch (Exception e) {
//                    log.error("订单处理异常",e);
//                    try {
//                        Thread.sleep(20);
//                    } catch (InterruptedException interruptedException) {
//                        interruptedException.printStackTrace();
//                    }
//                }
//            }
//        }
//    }



/*    private BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024*1024);
    private class VoucherOrderHandler implements Runnable{
        @Override
        public void run() {
            while (true){
                try {
                    //1.获取队列中的订单信息
                    VoucherOrder voucherOrder = orderTasks.take();
                    //2.创建订单
                    handleVoucherOrder(voucherOrder);
                } catch (Exception e) {
                    log.error("订单处理异常",e);
                }


            }
        }
    }*/

    private void handleVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        //创建锁对象
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        //获取锁
        boolean isLock = lock.tryLock();
        if (!isLock) {
            //获取锁失败，返回错误或者重试
            log.error("同一个用户不能下两单");
            return;
        }
        //synchronized (userId.toString().intern()) {
        try {
            proxy.createVoucherOrder(voucherOrder);
        } finally {
            //释放锁
            lock.unlock();
        }
    }


    private IVoucherOrderService proxy;
    @Override
    public Result seckillVoucher(Long voucherId){
        //获取用户
        Long userId = UserHolder.getUser().getId();
        //2.3订单ID
        long orderId = redisIdWorker.nextId("order");
        //1. 执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString(),String.valueOf(orderId)
        );
        //2. 判断结果是否为0
        int r = result.intValue();
        if(r != 0){
            //2.1 不为0,代表没有购买资格
            return Result.fail(r==1?"库存不足":"不能重复下单");
        }

        //3.获取代理对象
        proxy = (IVoucherOrderService) AopContext.currentProxy();

        //4.返回订单id
        return Result.ok(orderId);
    }

/*
    @Override
    public Result seckillVoucher(Long voucherId){
        //获取用户
        Long userId = UserHolder.getUser().getId();
        //1. 执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString()
        );
        //2. 判断结果是否为0
        int r = result.intValue();
        if(r != 0){
            //2.1 不为0,代表没有购买资格
            return Result.fail(r==1?"库存不足":"不能重复下单");
        }
        //2.2 为0，有购买资格，把下单信息保存到阻塞队列
        VoucherOrder voucherOrder = new VoucherOrder();
        //2.3订单ID
        long orderId = redisIdWorker.nextId("order");
        System.out.println(orderId);
        voucherOrder.setId(orderId);
        //2.4用户ID
        voucherOrder.setUserId(userId);
        //2.5优惠券ID
        voucherOrder.setVoucherId(voucherId);
        save(voucherOrder);
        //2.6放入阻塞队列
        orderTasks.add(voucherOrder);
        //3.获取代理对象
        proxy = (IVoucherOrderService) AopContext.currentProxy();

        //4.返回订单id
        return Result.ok(orderId);
    }
*/


/*
    @Override
    public Result seckillVoucher(Long voucherId) {
        //1.查询优惠券
        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
        //2.判断秒杀是否开始
        if(voucher.getBeginTime().isAfter(LocalDateTime.now())){
            //尚未开始
            return Result.fail("秒杀尚未开始");
        }
        //3.判断秒杀是否结束
        if(voucher.getEndTime().isBefore(LocalDateTime.now())){
            //秒杀已经结束
            return Result.fail("秒杀已经结束");
        }
        //4.判断库存是否充足
        if(voucher.getStock() < 1){
            return Result.fail("库存不足");
        }
        Long userId = UserHolder.getUser().getId();
        //创建锁对象
//        SimpleRedisLock lock = new SimpleRedisLock("order:" + userId, stringRedisTemplate);
        RLock lock = redissonClient.getLock("lock:order:" + userId);

        //获取锁
        boolean isLock = lock.tryLock();
        if (!isLock) {
            //获取锁失败，返回错误或者重试
            return Result.fail("同一个用户不能下两单");
        }
        //synchronized (userId.toString().intern()) {
            //获取代理对象
        try {
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId);
        } finally {
            //释放锁
            lock.unlock();
        }
        //}
    }
*/

    @Transactional
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        //5.一人一单
        Long userId = voucherOrder.getUserId();
        //5.1查询订单
        int count = query().in("user_id", userId).eq("voucher_id", voucherOrder.getVoucherId()).count();
        if(count != 0){
            //5.2用户已经购买过
            log.error("用户已经购买过一次了");
            return;
        }
        //6.扣减库存
        boolean success = seckillVoucherService.update().
                setSql("stock = stock - 1")//set stock = stock - 1
                .eq("voucher_id", voucherOrder.getVoucherId()).gt("stock",0)//where id = ? and stock > 0
                .update();
        if(!success){
            log.error("库存不足！");
            return;
        }
        //7.创建订单
        save(voucherOrder);
    }
}
