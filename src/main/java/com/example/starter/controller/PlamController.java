package com.example.starter.controller;

import com.example.starter.dto.PersonDTO;
import com.example.starter.dto.ResultDTO;
import com.example.starter.service.PersonService;
import com.fujitsu.frontech.palmsecure.util.PalmSecureException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author : bingrun.chiu
 * @description:
 * @date: 2020/1/8 13:38
 **/
@Api(description = "plam")
@RestController()
@RequestMapping("/plam")
public class PlamController {
    @Autowired
    PersonService personService;

    @ApiOperation(value = "录入", hidden = true)
    @GetMapping("/enroll")
    public void enroll(String path) {
        personService.enroll(path);
    }

    @ApiOperation(value = "录入")
    @PostMapping("/enroller")
    public ResultDTO enroller(@RequestBody PersonDTO personDTO) {
        var result = personService.enroller(personDTO);
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setResult(result);
        resultDTO.setMessage("成功");// todo:
        return resultDTO;
    }

    @ApiOperation("校验")
    @PostMapping("/verify")
    public ResultDTO verify(@RequestBody PersonDTO personDTO) throws PalmSecureException, IOException {
        ResultDTO resultDTO = new ResultDTO();
        String result = personService.verify(personDTO);
        if (result != null && ! result.isEmpty()) {
            resultDTO.setResult(true);
            resultDTO.setMessage(result);
        } else {
            resultDTO.setResult(false);
            resultDTO.setMessage("失败");
        }
        return resultDTO;
    }
}
