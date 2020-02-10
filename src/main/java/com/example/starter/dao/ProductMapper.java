package com.example.starter.dao;

import com.example.starter.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author : bingrun.chiu
 * @description:
 * @date: 2020/1/17 16:52
 **/
@Mapper
public interface ProductMapper {

    @Select({ "select * from product where deleted <> 1 order by weight desc" })
    List<Product> getAllProductOrderByWeightDesc();

    @Update({"update product set deleted = 1 where id = #{id}"})
    void updateDeleted(Integer id );
}
