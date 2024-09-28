package com.example.mapper;

import org.apache.ibatis.annotations.*;
import pojo.User;
import pojo.UserLoginDTO;
import pojo.UserResult;

import java.util.List;
import java.util.Map;

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

    /**
     * 更新胜负
     * @param userResult
     */
    void insertResult(UserResult userResult);

    @Select("select ${method1}, ${method2} from user where username='admin'")
    Map<String, Integer> getAdminElo(String method1, String method2);

    @Select("select CLBA, DA, HTBA, lsbPanda, refool, ours, Sig, clean, Inv, SAA from user where username = 'admin'")
    Map<String, Integer> getElo();
}
