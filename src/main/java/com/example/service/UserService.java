package com.example.service;

import org.springframework.stereotype.Service;
import pojo.User;
import pojo.UserDataDTO;
import pojo.UserLoginDTO;
import pojo.UserSaveSelectionDTO;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Map;


public interface UserService {
//    受试者登录
    User login(UserLoginDTO userLoginDTO);

//    受试者注册
    void register(UserLoginDTO userLoginDTO);

//    受试者数据保存
    Map<String, Integer> saveDate(UserDataDTO userDataDTO);
//受试者留言
    void submitMessage(String name, String message);

    /**
     * elo
     * @param userDataDTO
     * @return
     */
    Map<String, Integer> elo(UserDataDTO userDataDTO);

    /**
     * 保存便利的文件夹
     * @param userSaveSelectionDTO
     * @return
     */
     void saveSelectionHistory(UserSaveSelectionDTO userSaveSelectionDTO);

    /**
     * 获取分数
     * @return
     */
    Map<String, Integer> getElo();
}
