package com.xxz.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxz.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xzxie
 * @create 2022/11/16 19:24
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
