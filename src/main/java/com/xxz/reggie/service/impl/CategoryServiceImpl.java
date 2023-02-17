package com.xxz.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxz.reggie.common.CustomException;
import com.xxz.reggie.entity.Category;
import com.xxz.reggie.entity.Dish;
import com.xxz.reggie.entity.Setmeal;
import com.xxz.reggie.mapper.CategoryMapper;
import com.xxz.reggie.service.CategoryService;
import com.xxz.reggie.service.DishService;
import com.xxz.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xzxie
 * @create 2022/11/21 16:50
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 根据 id 删除分类，删除需进行分类与菜品和套餐的关联判断
     *
     * @param id 待删除分类的 id
     */
    @Override
    public void remove(Long id) {
        // 查询当前分类是否关联了菜品，如果已关联，则抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询添加，根据分类 id 查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int countDish = dishService.count(dishLambdaQueryWrapper);
        if (countDish > 0) {
            // 抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }


        // 查询当前分类是否关联了套餐，如果已关联，则抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int countSet = setmealService.count(setmealLambdaQueryWrapper);
        if (countSet > 0) {
            // 抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        // 正常删除
        super.removeById(id);
    }
}
