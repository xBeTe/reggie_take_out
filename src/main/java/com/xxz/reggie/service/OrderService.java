package com.xxz.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxz.reggie.entity.Orders;

/**
 * @author xzxie
 * @create 2023/2/17 16:08
 */
public interface OrderService extends IService<Orders> {

    // 用户下单
    void submit(Orders orders);
}
