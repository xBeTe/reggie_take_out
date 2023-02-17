package com.xxz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxz.reggie.common.R;
import com.xxz.reggie.dto.SetmealDto;
import com.xxz.reggie.entity.Category;
import com.xxz.reggie.entity.Dish;
import com.xxz.reggie.entity.Setmeal;
import com.xxz.reggie.service.CategoryService;
import com.xxz.reggie.service.SetmealDishService;
import com.xxz.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xzxie
 * @create 2022/11/24 19:44
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     *
     * @param setmealDto 请求的新增套餐信息
     * @return 处理结果
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 分页查询
     *
     * @param page     分页数
     * @param pageSize 分页大小
     * @param name     套餐名
     * @return 分页信息
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        // 分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        // 包含分类名的分页构造器
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 根据套餐名模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        // 添加是否删除过滤条件
        queryWrapper.eq(Setmeal::getIsDeleted, 0);
        // 添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        // 对象拷贝（除具体 records）
        BeanUtils.copyProperties(page, setmealDtoPage, "records");
        // 获取 records
        List<Setmeal> records = pageInfo.getRecords();
        // 通过 records 中的分类 id 查询 分类名，并赋值给 setmealDto
        List<SetmealDto> list = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        // 将包含分类名的 setmealDto 集合赋值给 setmealDtoPage
        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }

    /**
     * 修改套餐状态为已删除
     *
     * @param ids 待删除菜品的 id
     * @return 处理结果
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("ids = {}", ids);
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }

    /**
     * 修改套餐的停启售状态
     * @param status 请求修改的状态 0/1
     * @param ids 请求修改的状态的套餐的 id
     * @return 处理结果
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") int status,
                                  @RequestParam List<Long> ids) {
        log.info("status = {}, ids = {}", status, ids);
        if (status == 1 || status == 0) {
            setmealService.updateStatus(status, ids);
            return R.success("修改套餐状态成功");
        } else {
            return R.error("状态参数非法");
        }
    }

    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {

        log.info(setmealDto.toString());
        setmealService.updateWithSetmealDishes(setmealDto);
        return R.success("修改套餐成功");

    }

    /**
     * 根据条件查询套餐
     * @param setmeal 套餐查询条件
     * @return 符合条件的套餐数据
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
