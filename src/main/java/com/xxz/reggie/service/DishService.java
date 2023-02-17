package com.xxz.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxz.reggie.dto.DishDto;
import com.xxz.reggie.entity.Dish;

import java.util.List;

/**
 * @author xzxie
 * @create 2022/11/21 18:21
 */
public interface DishService extends IService<Dish> {

    // 新增菜品，同时插入菜品对应的口味数据
    void saveWithFlavor(DishDto dishDto);

    // 根据菜品 id 查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    void updateIsDelete(List<Long> ids);

    void updateStatus(int status, List<Long> ids);
}
