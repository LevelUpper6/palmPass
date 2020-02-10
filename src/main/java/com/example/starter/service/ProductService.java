package com.example.starter.service;

import com.example.starter.dao.ProductMapper;
import com.example.starter.entity.Product;
import com.example.starter.utils.RedisUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author : bingrun.chiu
 * @description: 商品service
 * @date: 2020/1/17 14:37
 **/
@Service
public class ProductService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired(required = false)
    ProductMapper productMapper;

    public List<Product> findProductList(BigDecimal weight, String id) {
        List<Product> products = Lists.newArrayList();

        if (redisUtil.hasKey(id)) {// 如果redis有key,是关门.
            BigDecimal preWeight = new BigDecimal(redisUtil.get(id).toString());
            // 算法:
            final BigDecimal[] difference = {preWeight.subtract(weight).add(new BigDecimal(10))};//pre - weight + 20, 误差是20g
            productMapper.getAllProductOrderByWeightDesc().stream().forEachOrdered(item -> {
                if (difference[0].compareTo(item.getWeight()) > 0) {
                    products.add(item);
                    System.out.println("....." + "product = " + item.getName());
                    difference[0] = difference[0].subtract(item.getWeight());
                    productMapper.updateDeleted(item.getId());
                }
            });

            redisUtil.del(id);// 关完门,一次会话结束,清除掉缓存.
        } else {// 没有key, 是开门,
            redisUtil.set(id, weight.toPlainString());// 一次会话的开始,设置缓存.
        }

        return products;
    }
}
