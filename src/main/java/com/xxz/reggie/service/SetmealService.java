package com.xxz.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxz.reggie.dto.SetmealDto;
import com.xxz.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author xzxie
 * @create 2022/11/21 18:22
 */
public interface SetmealService extends IService<Setmeal> {

    // 新增套餐，同时保存套餐和菜品的关联关系
    void saveWithDish(SetmealDto setmealDto);

    // 删除套餐，同时删除套餐和菜品对应的关联数据
    void removeWithDish(List<Long> ids);

    void updateStatus(int status, List<Long> ids);

    SetmealDto getByIdWithDish(Long id);

    void updateWithSetmealDishes(SetmealDto setmealDto);
}
