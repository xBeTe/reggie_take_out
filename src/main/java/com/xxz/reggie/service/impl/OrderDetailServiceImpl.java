package com.xxz.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxz.reggie.entity.OrderDetail;
import com.xxz.reggie.mapper.OrderDetailMapper;
import com.xxz.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author xzxie
 * @create 2023/2/17 16:11
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
