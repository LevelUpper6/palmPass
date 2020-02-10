package com.example.starter.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author : bingrun.chiu
 **/
@Data
public class UserEntity {
    private long userId;
    private String userCode;
    private String userName;
    private String nickName;
    private String userPwd;
    private Date createDate;
    private Date updateDate;
}
