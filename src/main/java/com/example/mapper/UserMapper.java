package com.example.mapper;

import org.apache.ibatis.annotations.*;
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

    @Update("UPDATE user SET leaveMessage = #{message} WHERE username = #{name}")
    void updateMessage(@Param("name") String name, @Param("message") String message);

    @Select("select * from user where id < #{currentUserId} order by id desc limit 1")
    User getPreviousUser(Long currentUserId);
}
