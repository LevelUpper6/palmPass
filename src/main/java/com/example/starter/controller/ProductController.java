package com.example.starter.controller;

import com.example.starter.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author : bingrun.chiu
 * @description: 商品
 * @date: 2020/1/17 14:34
 **/
@Api(description = "商品接口")
@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @ApiOperation("传入重量")
    @PostMapping("/weight")
    public String getWeight(Model model, @RequestParam String weight, String id) {
        var a = Integer.parseInt(setStr(weight),16);
//        System.out.println("....." + "a = " + a);
        BigDecimal param = new BigDecimal(Integer.parseInt(setStr(weight),16));

        var productList = productService.findProductList(param, id);
        model.addAttribute("productList", productList);

        return "productList";
    }

    private static String setStr(String str) {
        char[] aa = str.toCharArray();
        int j = aa.length / 2;
        int k = aa.length % 2;
        int le = j + k;
        String[] bbb = new String[le];
        String bb = "";
        for (int i = 0; i < j; i++) {
            bbb[i] = aa[2 * i] + "" + aa[2 * i + 1];
        }
        if (k == 1) {
            bbb[j] = aa[aa.length - 1] + "";
        }
        int i = 0;
        for (int p = bbb.length-1; p >= 0; p--) {
            System.out.println(bbb[p]);
            bb= bb +bbb[p].toString();
//            i++;
        }
        return bb;
    }

    public static void main(String... args) {
        var bb = setStr("1234567");
        System.out.println("....." + "bb = " + bb);
    }
}