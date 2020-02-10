package com.example.starter.controller;

import com.example.starter.entity.UserEntity;
import com.example.starter.service.UserService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : bingrun.chiu
 **/
@Api(description = "user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation("userlist")
    @GetMapping("/userlist")
    public List<UserEntity> queryList() {
        PageHelper.startPage(1, 2);
        return userService.queryList();
    }

    @ApiOperation("queryUser")
    @GetMapping("/queryUser")
    public UserEntity queryUserEntity(long userId) {
        UserEntity userEntity = userService.findById(userId);
        return userEntity;
    }

    @ApiOperation("insert")
    @PostMapping("/insert")
    public int insertEntity() {
        return userService.insertEntity();
    }

    @ApiOperation("insertParam")
    @PostMapping("/insertParam")
    public int insertParam() {
        return userService.insertParam();
    }

    @ApiOperation("insertByMap")
    @PostMapping("/insertByMap")
    public int insertByMap() {
        return userService.insertByMap();
    }

    @ApiOperation("updateEntity")
    @PostMapping("/updateEntity")
    public int updateEntity() {
        return userService.updateEntity();
    }

    @ApiOperation("deleteEntity")
    @PostMapping("/deleteEntity")
    public int deleteEntity() {
        return userService.deleteEntity();
    }
}
