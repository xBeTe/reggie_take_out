package com.xxz.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxz.reggie.entity.User;
import com.xxz.reggie.mapper.UserMapper;
import com.xxz.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author xzxie
 * @create 2022/12/27 10:28
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
