package com.bjpowernode.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.seckill.commons.CommonsConstants;
import com.bjpowernode.seckill.commons.CommonsReturnObject;
import com.bjpowernode.seckill.mapper.GoodsMapper;
import com.bjpowernode.seckill.model.Goods;
import com.bjpowernode.seckill.service.GoodsService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Component
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<Goods> getAll() {
        return goodsMapper.getAll();
    }

    @Override
    public Goods getGoodsById(Integer id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public CommonsReturnObject secKill(Integer goodsId, String radomName, Integer uid) {
        CommonsReturnObject returnObject = new CommonsReturnObject();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);

        String strStore = redisTemplate.opsForValue().get(CommonsConstants.SECKILL_STORE + radomName);
        if (null == strStore) {
            returnObject.setCode(CommonsConstants.ERROR);
            returnObject.setErrorMessage("当前商品不可秒杀");
            return returnObject;
        }
        Integer store = Integer.valueOf(strStore);
        if (0 >= store) {
            returnObject.setCode(CommonsConstants.ERROR);
            returnObject.setErrorMessage("库存不足");
            return returnObject;
        }
        String isUserShop = redisTemplate.opsForValue().get(CommonsConstants.HANDLE_USER + radomName + uid);
        if (null != isUserShop) {
            returnObject.setCode(CommonsConstants.ERROR);
            returnObject.setErrorMessage("该商品只能秒杀一次");
            return returnObject;
        }
        String limitingList = redisTemplate.opsForValue().get(CommonsConstants.LIMITING_LIST);
        if (null == limitingList || Integer.valueOf(limitingList) > 1000) {
            returnObject.setCode(CommonsConstants.ERROR);
            returnObject.setErrorMessage("对不起，服务器繁忙请稍后再试");
            return returnObject;
        }
        redisTemplate.opsForValue().increment(CommonsConstants.LIMITING_LIST);

        List<String > watch = new ArrayList<>();
        watch.add(CommonsConstants.SECKILL_STORE+radomName);
        redisTemplate.watch(watch);
        return null;
    }
}
