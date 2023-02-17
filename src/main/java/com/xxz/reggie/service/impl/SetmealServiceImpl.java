package com.xxz.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxz.reggie.common.CustomException;
import com.xxz.reggie.dto.SetmealDto;
import com.xxz.reggie.entity.Setmeal;
import com.xxz.reggie.entity.SetmealDish;
import com.xxz.reggie.mapper.SetmealMapper;
import com.xxz.reggie.service.SetmealDishService;
import com.xxz.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xzxie
 * @create 2022/11/21 18:24
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     *
     * @param setmealDto 包含菜品信息的套餐信息
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息到 setmeal 表
        this.save(setmealDto);

        // 保存套餐和菜品的关联信息到 setmaeal_dish 表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        // 可以删除，先删除 setmeal 表中的数据
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Setmeal::getIsDeleted, 1).in(Setmeal::getId, ids);
        this.update(null, updateWrapper);

        // 删除 setmael_dish 表中的数据
        LambdaUpdateWrapper<SetmealDish> setmealDishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        setmealDishLambdaUpdateWrapper.set(SetmealDish::getIsDeleted, 1).in(SetmealDish::getSetmealId, ids);
        setmealDishService.update(null, setmealDishLambdaUpdateWrapper);


    }

    @Override
    public void updateStatus(int status, List<Long> ids) {
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Setmeal::getStatus, status).in(Setmeal::getId, ids);
        this.update(null, updateWrapper);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        // 查询套餐基本信息
        Setmeal setmeal = this.getById(id);

        // 将套餐的基本信息拷贝至 dishDto
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        // 查询套餐对应的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);

        // 为 setmealDto 赋值 dishes
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    @Override
    public void updateWithSetmealDishes(SetmealDto setmealDto) {
        // 更新套餐基本信息
        this.updateById(setmealDto);

        // 清理当前套餐对应的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);


        // 获取菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        // 套餐菜品关联套餐 id
        setmealDishes = setmealDishes.stream().map(item -> {
            Long setmealDtoId = setmealDto.getId();
            item.setSetmealId(setmealDtoId);
            return item;
        }).collect(Collectors.toList());

        // 添加当前提交的菜品信息
        setmealDishService.saveBatch(setmealDishes);

    }


}
