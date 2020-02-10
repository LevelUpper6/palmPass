package com.example.starter.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : bingrun.chiu
 * @description:
 * @date: 2020/1/8 14:02
 **/
@Data
@ApiModel(description = "Person")
public class PersonDTO {
    @ApiModelProperty(value = "主键", hidden = true)
    private Long id;
    @ApiModelProperty("用户手机号后4位")
    private String phoneNumber;
    @ApiModelProperty("分数")
    private Integer score;
    @ApiModelProperty("静脉数据,8到15k的十六进制数据")
    private String veinData;//静脉数据文件绝对路径
}
