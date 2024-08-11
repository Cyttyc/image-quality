package com.example.service;

import org.springframework.stereotype.Service;
import pojo.User;
import pojo.UserDataDTO;
import pojo.UserLoginDTO;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Map;


public interface UserService {
//    受试者登录
    User login(UserLoginDTO userLoginDTO);

//    受试者注册
    void register(UserLoginDTO userLoginDTO);

//    受试者数据保存
    void saveDate(UserDataDTO userDataDTO);
}
