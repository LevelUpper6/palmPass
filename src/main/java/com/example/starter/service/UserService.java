package com.example.starter.service;

import com.example.starter.dao.UserMapper;
import com.example.starter.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : bingrun.chiu
 **/
@Service
public class UserService {
    @Autowired(required = false)
    private  UserMapper mapper;

    public List<UserEntity> queryList() {
        List<UserEntity> userList = mapper.queryList();
        return userList;
    }

    public UserEntity findById(long userId) {
        System.out.println("userId:" + userId);
        return mapper.findById(userId);
    }

    public int insertEntity() {
        UserEntity entity = new UserEntity();
        entity.setUserName("lisi");
        entity.setUserCode("lisi" + new Date());
        entity.setNickName("郭靖");
        entity.setUserPwd("123");
        entity.setCreateDate(new Date());
        entity.setUpdateDate(new Date());
        return mapper.insertEntity(entity);
    }

    public int insertParam() {
        return mapper.insertParam("linzhiqiang", "lzq");
    }

    public int insertByMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("nickName", "zhaotong");
        map.put("userCode", "zt");
        return mapper.insertByMap(map);
    }

    public int updateEntity() {
        UserEntity entity = new UserEntity();
        entity.setUserId(1);
        entity.setNickName("郭靖");
        return mapper.updateEntity(entity);
    }

    public int deleteEntity() {
        UserEntity entity = new UserEntity();
        entity.setUserId(11);
        return mapper.deleteEntity(entity);
    }
}
