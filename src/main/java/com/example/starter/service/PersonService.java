package com.example.starter.service;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;
import com.example.starter.SDK.SDKProxy;
import com.example.starter.dao.PersonMapper;
import com.example.starter.dto.PersonDTO;
import com.example.starter.entity.Person;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.xml.bind.DatatypeConverter;

/**
 * @author : bingrun.chiu
 * @description:
 * @date: 2020/1/8 13:58
 **/
@Service
@Slf4j
public class PersonService {

    @Autowired(required = false)
    PersonMapper personMapper;
    @Autowired
    SDKProxy sdkProxy;
    String dir = "/dat";

    public String verify(PersonDTO personDTO) {
/*        // 测试用代码
        var bytes = sdkProxy.readFile("C:\\Users\\chiu\\Desktop\\plamPass\\Demo\\CaptureData\\12_Capture_R.dat");
        var str = Base64Encoder.encode(bytes);
        personDTO.setVeinData(str);*/

        return sdkProxy.identifyMatch(personDTO.getVeinData());
    }

    public void enroll(String absolutePath) {
        log.info("文件绝对路径: {}", absolutePath);
        String name = FileUtil.getName(absolutePath);
        var array = StringUtils.split(name, "_");
        var phoneNumber = array != null ? array[1].replace("_", "").replace(".dat", "").trim() : null;
        Person person = new Person();
        person.setPhoneNumber(Integer.parseInt(phoneNumber));
        person.setVeinData(absolutePath);
        log.info("phoneNumber: {}", phoneNumber);
        int countNumber = personMapper.countNumber(phoneNumber);
        if (countNumber == 0) {
            personMapper.insertPerson(person);
        } else if (countNumber > 0) {
            log.info("phoneNumber: {} 已经存在", phoneNumber);
        }
    }

    public boolean enroller(PersonDTO personDTO) {
        try {
            var file = FileUtil.newFile(dir + "12_" + personDTO.getPhoneNumber().substring(personDTO.getPhoneNumber().length() - 4) + ".dat");
//          FileUtil.writeBytes(Base64Decoder.decode(personDTO.getVeinData()),file.getAbsolutePath());
            // https://www.jianshu.com/p/4c1c70cc26f6
            var bytes = DatatypeConverter.parseBase64Binary(personDTO.getVeinData());// 把Base64编码的字符串转换成byte数组
            FileUtil.writeBytes(bytes, file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}