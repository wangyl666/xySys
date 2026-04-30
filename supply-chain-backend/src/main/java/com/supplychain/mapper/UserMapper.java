package com.supplychain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
