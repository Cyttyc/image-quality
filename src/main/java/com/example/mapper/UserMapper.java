package com.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import pojo.User;
import pojo.UserLoginDTO;

@Mapper
public interface UserMapper {

    @Select("select * from user where username=#{username};")
    User getByUsername(String username);

    @Insert("insert into user (username, password, create_time, update_time) VALUES " +
            "(#{username}, #{password}, #{createTime}, #{updateTime})")
    void register(User user);

    void update(User user);
}
