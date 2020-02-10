package com.example.starter.dao;

import com.example.starter.entity.Person;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author : bingrun.chiu
 * @description:
 * @date: 2020/1/8 13:59
 **/
@Mapper
public interface PersonMapper {

    @Insert({"insert into person (phone_number,vein_data) values (#{phoneNumber},#{veinData})"})
    long insertPerson(Person person);

    @Select({"select count(id) from person where phone_number = #{phoneNumber}"})
    int countNumber(String phone_number);

    @Select({"select vein_data from person"})
    List<String> getAllVeinData();

    @Select({"select count(*) from person"})
    Integer countPerson();
}
