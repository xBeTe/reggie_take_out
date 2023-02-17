package com.xxz.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxz.reggie.entity.ShoppingCart;
import com.xxz.reggie.mapper.ShoppingCartMapper;
import com.xxz.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author xzxie
 * @create 2023/2/16 15:46
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
