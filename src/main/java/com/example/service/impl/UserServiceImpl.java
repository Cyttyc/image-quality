package com.example.service.impl;
import java.lang.reflect.Method;
import com.example.exception.AccountExistException;
import com.example.exception.AccountNotFoundException;
import com.example.exception.BaseException;
import com.example.exception.PasswordErrorException;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import pojo.User;
import pojo.UserDataDTO;
import pojo.UserLoginDTO;

import java.time.LocalDateTime;
import java.util.Map;
//git remote add origin https://github.com/Cyttyc/image-quality.git


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(UserLoginDTO userLoginDTO){
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1.根据用户名查询数据库的数据
        User user = userMapper.getByUsername(username);
        //2.处理各种异常情况（用户名不存在/密码不对/账户被锁定）
        if(user == null){
            throw new AccountNotFoundException("账号不存在");
        }

        //密码对比
        //对前端传来的明文密码进行加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if(!password.equals(user.getPassword())){
            //密码错误
            throw new PasswordErrorException("密码错误");
        }

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
        User user = new User();
        BeanUtils.copyProperties(userLoginDTO, user);
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.register(user);
    }

//    受试者实验数据保存
    public void saveDate(UserDataDTO userDataDTO) {
        String username = userDataDTO.getUsername();
        Map<String, Integer> data = userDataDTO.getFolderCounts();
        User user = userMapper.getByUsername(username);

        if(user == null){
            throw new AccountNotFoundException("账号不存在！");
        }
        user.setUpdateTime(LocalDateTime.now());
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
    }
}
