package com.xxz.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxz.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xzxie
 * @create 2022/12/27 10:26
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
