package com.bjpowernode.seckill.scheduled;


import com.bjpowernode.seckill.commons.CommonsConstants;
import com.bjpowernode.seckill.mapper.GoodsMapper;
import com.bjpowernode.seckill.model.Goods;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@EnableScheduling
@Component
public class SecKillScheduled {
    @Resource
    private RedisTemplate<String ,String > redisTemplate;
    @Resource
    private GoodsMapper goodsMapper;
    @Scheduled(cron = "0/5 * * * * *")
    public void initSecKillGoods(){
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setKeySerializer(stringRedisSerializer);


        List<Goods> all = goodsMapper.getAll();
        for (Goods goods : all){
            if (null != redisTemplate.opsForValue().get(CommonsConstants.SECKILL_STORE+goods.getRandomName())){
                continue;
            }
            redisTemplate.opsForValue().set(CommonsConstants.SECKILL_STORE+goods.getRandomName(), goods.getStore().toString());
        }
    }
}
