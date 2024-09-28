package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DatabaseHelper {


    @Select("SELECT ${round_i} from user_results where id=#{id}")
    Integer getResult(int id, String round_i);
}
