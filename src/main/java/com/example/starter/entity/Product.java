package com.example.starter.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : bingrun.chiu
 * @description: 商品实体类
 * @date: 2020/1/17 14:44
 **/
@Data
public class Product {
    private int id;
    private String name;
    private BigDecimal weight;
    private int deleted;
}
