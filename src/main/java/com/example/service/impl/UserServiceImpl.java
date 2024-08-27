package com.example.service.impl;
import java.lang.reflect.Method;

import com.example.exception.*;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import pojo.User;
import pojo.UserDataDTO;
import pojo.UserLoginDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
//git remote add origin https://github.com/Cyttyc/image-quality.git

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(UserLoginDTO userLoginDTO){
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1.根据用户名查询数据库的数据
        User user = userMapper.getByUsername(username);
        Integer status = user.getStatus();
        //2.处理各种异常情况（用户名不存在/密码不对/账户被锁定）
        if(user == null){
            throw new AccountNotFoundException("账号不存在");
        }
        if(status==1){
            throw new AccountNotFoundException("账户已被禁用！");
        }

        //密码对比
        //对前端传来的明文密码进行加密
//        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if(!password.equals(user.getPassword())){
            //密码错误
            throw new PasswordErrorException("密码错误");
        }
//ngrok http --domain=profound-warthog-admittedly.ngrok-free.app 9090
        return user;
    }

//   受试者注册
    public void register(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        //根据用户姓名查询是否存在重复用户
        User beforeUser = userMapper.getByUsername(username);
        if(beforeUser != null){
            throw new AccountExistException("用户名已存在");
        }
        if(username.length()>7){
            throw new AccountNameTooLong("用户名长度超出范围");
        }
        String regex = "^[a-zA-Z0-9]+$";

        if (!(username).matches(regex)) {
            throw new AccountNameTooLong("用户名只能包含字母和数字");
        }
        User user = new User();
        BeanUtils.copyProperties(userLoginDTO, user);
//        user.setPassword(user.getPassword());
//        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.register(user);
    }

//    受试者实验数据保存
    public Map<String, Integer> saveDate(UserDataDTO userDataDTO) {
        String username = userDataDTO.getUsername();
        Map<String, Integer> data = userDataDTO.getEloScores();
        User user = userMapper.getByUsername(username);

        if(user == null){
            throw new AccountNotFoundException("账号不存在！");
        }
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus(1);
        //根据data中的数据，更新用户表中的数据
        for(Map.Entry<String, Integer> entry : data.entrySet()){
            String key = entry.getKey();
            Integer value = entry.getValue();
            // 使用反射机制更新属性
            String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
            try {
                Method method = User.class.getMethod(methodName, Integer.class);
                method.invoke(user, value);
            } catch (Exception e) {
                // 如果找不到对应的 setter 方法，记录错误或处理异常
                System.err.println("Failed to set property " + key + ": " + e.getMessage());
            }
        }
        // 更新User对象到数据库
        userMapper.update(user);
        Map<String, Integer> map = new HashMap<>();
        map.put("status", user.getStatus());
        return map;
    }

//    受试者留言
    public void submitMessage(String name, String message) {
        userMapper.updateMessage(name, message);
    }

    /**
     * elo
     * @param userDataDTO
     * @return
     */
    public Map<String, Integer> elo(UserDataDTO userDataDTO) {
        String username = userDataDTO.getUsername();
        User currentUser = userMapper.getByUsername(username);
        // 获取上一个用户的ELO分数
        Map<String, Integer> eloScores = new HashMap<>();
        if (currentUser == null) {
            throw new AccountNotFoundException("账号不存在！");
        }

        if(username.equals("admin")){
            // 如果是 admin 用户，所有 ELO 分数均为 1000
            eloScores.put("CLBA", 1000);
            eloScores.put("DA", 1000);
            eloScores.put("HTBA", 1000);
            eloScores.put("lsbPanda", 1000);
            eloScores.put("refool", 1000);
            eloScores.put("ours", 1000);
            eloScores.put("Sig", 1000);
            eloScores.put("clean", 1000);
            eloScores.put("Inv", 1000);
            eloScores.put("SAA", 1000);
        }else{
            // 获取当前用户ID
            Long currentUserId = currentUser.getId();

            // 查询数据库中ID小于当前用户ID的最近一条记录，即上一个用户
            User previousUser = userMapper.getPreviousUser(currentUserId);

            if (previousUser == null) {
                throw new AccountNotFoundException("没有找到上一个用户的记录！");
            }

            eloScores.put("CLBA", previousUser.getCLBA());
            eloScores.put("DA", previousUser.getDA());
            eloScores.put("HTBA", previousUser.getHTBA());
            eloScores.put("lsbPanda", previousUser.getLsbPanda());
            eloScores.put("refool", previousUser.getRefool());
            eloScores.put("ours", previousUser.getOurs());
            eloScores.put("Sig", previousUser.getSig());
            eloScores.put("clean", previousUser.getClean());
            eloScores.put("Inv", previousUser.getInv());
            eloScores.put("SAA", previousUser.getSAA());
        }
        log.info("用户和数据：{}", eloScores);
        return eloScores;
    }
}
