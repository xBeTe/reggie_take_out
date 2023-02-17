package com.xxz.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxz.reggie.common.CustomException;
import com.xxz.reggie.dto.DishDto;
import com.xxz.reggie.entity.Dish;
import com.xxz.reggie.entity.DishFlavor;
import com.xxz.reggie.mapper.DishMapper;
import com.xxz.reggie.service.DishFlavorService;
import com.xxz.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xzxie
 * @create 2022/11/21 18:23
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时插入菜品对应的口味数据
     *
     * @param dishDto 包含菜品和口味的数据对象
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表 dish
        this.save(dishDto);

        Long dishId = dishDto.getId();  // 菜品 id

        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 菜品口味关联菜品 id
        flavors = flavors.stream().peek((item) -> item.setDishId(dishId)).collect(Collectors.toList());


        // 保存菜品口味数据到菜品口味表 dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据菜品 id 查询菜品信息和对应的口味信息
     *
     * @param id 菜品 id
     * @return 查询结果
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息
        Dish dish = this.getById(id);

        // 将菜品基本信息拷贝至 dishDto
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        // 查询菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        // 为 dishDto 赋值 flavors
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新菜品表基本信息
        this.updateById(dishDto);
        // 清理当前菜品对应的口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // 获取口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 菜品口味关联菜品 id
        flavors = flavors.stream().peek((item) -> item.setDishId(dishDto.getId())).collect(Collectors.toList());
        // 添加当前体提交的口味数据
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    @Transactional
    public void updateIsDelete(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getIsDeleted, 1).in(Dish::getId, ids);
        this.update(null, updateWrapper);

        LambdaUpdateWrapper<DishFlavor> dishFlavorLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        dishFlavorLambdaUpdateWrapper.set(DishFlavor::getIsDeleted, 1).in(DishFlavor::getDishId, ids);
        dishFlavorService.update(null, dishFlavorLambdaUpdateWrapper);
    }

    @Override
    public void updateStatus(int status, List<Long> ids) {
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getStatus, status).in(Dish::getId, ids);
        this.update(null, updateWrapper);
    }
}
