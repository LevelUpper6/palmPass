package com.example.starter.SDK;

import cn.hutool.core.codec.Base64;
import com.example.starter.dao.PersonMapper;
import com.fujitsu.frontech.palmsecure.util.PalmSecureException;
import com.fujitsu.frontech.palmsecure_smpl.exception.PsAplException;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : bingrun.chiu
 * @description: SDK工具方法代理
 * @date: 2020/2/12 10:08
 **/

@Slf4j
@Component
public class SDKProxy implements SDKTools {

    @Autowired(required = false)
    private PersonMapper personMapper;
    @Autowired
    private SDKImplementer sdkImplementer;

    @Override
    public boolean init() {
        log.info("SDK初始化...");
        return sdkImplementer.Ps_Sample_Apl_Java();
    }

    @Override
    public void terminal() {
        sdkImplementer.terminal();
        log.info("SDK已终止.");
    }

    @Override
    public byte[] readFile(String filename) {
        try {
            log.info("dat文件路径: {}",filename);
            return sdkImplementer.readFile(filename);
        } catch (PsAplException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public String identifyMatch(String veinData) {
        List<String> idList = new ArrayList<>();
        var suspects = personMapper.getAllVeinData().stream().map(item -> {
            byte[] bytes;
            bytes = this.readFile(item);
            var phoneNumber = StringUtils.split(item, "_")[1].replace("_", "").replace(".dat", "").trim();
            idList.add(phoneNumber);
            return bytes;
        }).map(Base64::encode).toArray(String[]::new);
        try {
            return sdkImplementer.identifyMatch(veinData, suspects, idList);
        } catch (PalmSecureException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
