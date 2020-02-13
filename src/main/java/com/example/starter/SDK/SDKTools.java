package com.example.starter.SDK;

/**
 * @author : bingrun.chiu
 * @description: SDK工具方法
 * @date: 2020/2/12 9:56
 **/
interface SDKTools {

    // 初始化
    boolean init();

    // 终止
    void terminal();

    // 读取dat文件
    byte[] readFile(String filename);

    // 匹配
    String identifyMatch(String veinData);
}
