package com.example.controller;

import com.example.properties.JwtProperties;
import com.example.service.UserService;
import com.example.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pojo.User;
import pojo.UserDataDTO;
import pojo.UserLoginDTO;
import result.Result;

import javax.security.auth.login.AccountNotFoundException;
import java.util.HashMap;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:9090") // 替换为前端的 URL
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("员工登录：{}", userLoginDTO);
        User res = userService.login(userLoginDTO);
        //登录成功后，生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", res.getId());
        claims.put("name", res.getUsername());
        claims.put("password", res.getPassword());
        String jwt = JwtUtils.createJWT(jwtProperties.getSubjectSecretKey(),
                jwtProperties.getSubjectTtl(),
                claims);
        HashMap<String, Object> responseData = new HashMap<>();
        responseData.put("token", jwt);
        responseData.put("username", res.getUsername());
        return Result.success(responseData);
    }

    @PostMapping("/register")
    public Result<Map<String, String>> register(@RequestBody UserLoginDTO userLoginDTO){
        log.info("受试者注册：{}", userLoginDTO);
        userService.register(userLoginDTO);
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        HashMap<String, String> result = new HashMap<>();
        result.put("username", username);
        result.put("password", password);
        return Result.success(result);
    }

    @PostMapping("/savedata")
    public Result saveData(@RequestBody UserDataDTO userDataDTO){
        log.info("用户和数据：{}", userDataDTO);
        userService.saveDate(userDataDTO);
        return Result.success();
    }
}
