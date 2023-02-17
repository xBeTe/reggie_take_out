package com.xxz.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxz.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xzxie
 * @create 2023/2/17 16:06
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
