package com.xxz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxz.reggie.common.R;
import com.xxz.reggie.dto.DishDto;
import com.xxz.reggie.entity.Category;
import com.xxz.reggie.entity.Dish;
import com.xxz.reggie.service.CategoryService;
import com.xxz.reggie.service.DishFlavorService;
import com.xxz.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xzxie
 * @create 2022/11/22 17:10
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品信息
     *
     * @param dishDto 前端请求的新增菜品的信息
     * @return 处理结果
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        // 清理所有的菜品缓存数据
        // Set keys = redisTemplate.keys("dish_*");
        // redisTemplate.delete(keys);

        // 清理当前修改菜品分类
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }

    /**
     * 查询包含菜品分类的菜品分页信息
     *
     * @param page     当前页
     * @param pageSize 每页数量
     * @param name     菜品名称关键词
     * @return 分页信息
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        // 分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<DishDto>();
        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        // 添加是否删除过滤条件
        queryWrapper.eq(Dish::getIsDeleted, 0);
        // 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行分页查询
        dishService.page(pageInfo, queryWrapper);

        // 对象拷贝，除 records 属性
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        // 取出分页信息中的 records
        List<Dish> records = pageInfo.getRecords();
        // 将 records 中数据根据分类 id 查询 分类名后，再赋值给 list
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId(); // 分类 id
            // 根据 id 查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());
        // 将带有分类名的 list 赋值给 新的分页信息
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据菜品 id 查询菜品信息和对应的口味信息
     *
     * @param id 菜品 id
     * @return 查询结果
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品信息
     *
     * @param dishDto 前端请求的修改菜品信息
     * @return 处理结果
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        // 清理所有的菜品缓存数据
        // Set keys = redisTemplate.keys("dish_*");
        // redisTemplate.delete(keys);

        // 清理当前修改菜品分类
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }

    /**
     * 修改菜品状态为已删除
     *
     * @param ids 待删除菜品的 id
     * @return 处理结果
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        log.info("ids = {}", ids);
        dishService.updateIsDelete(ids);

        // 清理所有的菜品缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return R.success("删除菜品成功");
    }

    /**
     * 修改菜品的停启售状态
     *
     * @param status 请求修改的状态 0/1
     * @param ids    请求修改的状态的菜品的 id
     * @return 处理结果
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") int status,
                                  @RequestParam("ids") List<Long> ids) {
        log.info("status = {}, ids = {}", status, ids);
        if (status == 1 || status == 0) {

            dishService.updateStatus(status, ids);

            // 清理所有的菜品缓存数据
            Set keys = redisTemplate.keys("dish_*");
            redisTemplate.delete(keys);

            return R.success("修改菜品状态成功");
        } else {
            return R.error("状态参数非法");
        }
    }

    /**
     * 根据条件查询菜品数据
     *
     * @param dish 请求的菜品的条件
     * @return 查询结果
     */
    /*@GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {

        // 查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 根据分类 id 查询
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 根据分类 name 查询
        queryWrapper.like(dish.getName() != null, Dish::getName, dish.getName());
        // 查询在售状态的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        List<DishDto> dishDtoList = null;

        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        // 先从 redis 中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList != null) {
            // 如果存在，直接返回，无需查询
            return R.success(dishDtoList);
        }

        // 如果不存在，需要查询数据库，并将查询到的菜品数据缓存到 redis


        // 查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 根据分类 id 查询
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 根据分类 name 查询
        queryWrapper.like(dish.getName() != null, Dish::getName, dish.getName());
        // 查询在售状态的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            // DishDto dishDto = new DishDto();

            // BeanUtils.copyProperties(item, dishDto);

            Long dishId = item.getId();

            /*LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);*/
            DishDto dishDto = dishService.getByIdWithFlavor(dishId);
            return dishDto;
        }).collect(Collectors.toList());

        // 将查询到的菜品数据缓存到 redis
        redisTemplate.opsForValue().set(key, dishDtoList, 60L, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
