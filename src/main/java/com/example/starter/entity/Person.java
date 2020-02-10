package com.example.starter.entity;

import lombok.Data;

/**
 * @author : bingrun.chiu
 * @description:
 * @date: 2020/1/8 13:40
 **/
@Data
public class Person {
    private Long id;//主键
    private Integer phoneNumber;//手机号后4位
    private String veinData;//静脉数据文件的绝对路径
}
