package com.example.starter.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author : bingrun.chiu
 * @description: 返回值
 * @date: 2020/1/10 14:57
 **/
@Data
@ApiModel(description = "返回值")
public class ResultDTO {
//    private int code;
    private boolean result;
    private String message;
}
