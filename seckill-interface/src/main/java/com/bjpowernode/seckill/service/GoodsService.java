package com.bjpowernode.seckill.service;

import com.bjpowernode.seckill.commons.CommonsReturnObject;
import com.bjpowernode.seckill.model.Goods;

import java.util.List;

public interface GoodsService {
    List<Goods> getAll();

    Goods getGoodsById(Integer id);

    CommonsReturnObject secKill(Integer goodsId, String radomName, Integer uid);
}
