package com.xxz.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxz.reggie.entity.Employee;
import com.xxz.reggie.mapper.EmployeeMapper;
import com.xxz.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author xzxie
 * @create 2022/11/16 19:29
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
