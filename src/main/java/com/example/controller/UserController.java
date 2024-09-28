package com.example.controller;

import com.example.properties.JwtProperties;
import com.example.service.UserService;
import com.example.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pojo.*;
import result.Result;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
        responseData.put("status", res.getStatus());
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
    public Result<Map<String, Integer>> saveData(@RequestBody UserDataDTO userDataDTO){
        log.info("用户和数据：{}", userDataDTO);
        Map<String, Integer> map = userService.saveDate(userDataDTO);
        return Result.success(map);
    }

    @PostMapping("/submitMessage")
    public Result submitMessage(@RequestBody MeaageDTO messageDTO){
        String name = messageDTO.getUsername();
        String message = messageDTO.getMessage();
        log.info("{}, 的留言：{}", name, message);
        userService.submitMessage(name, message);
        return Result.success();
    }

    @PostMapping("/elo")
    public Result<Map<String, Integer>> elo(@RequestBody UserDataDTO userDataDTO){
        Map<String, Integer> map = userService.elo(userDataDTO);
        return Result.success(map);
    }

    @PostMapping("/saveSelectionHistory")
    public void saveSelectionHistory(@RequestBody UserSaveSelectionDTO userSaveSelectionDTO){
        userService.saveSelectionHistory(userSaveSelectionDTO);

    }

    @PostMapping("/getElo")
    public Map<String, Integer> getElo(){
        return userService.getElo();
    }

}
