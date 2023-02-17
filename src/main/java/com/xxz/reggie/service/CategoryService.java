package com.xxz.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxz.reggie.entity.Category;

/**
 * @author xzxie
 * @create 2022/11/21 16:49
 */
public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
