package com.xxz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxz.reggie.common.R;
import com.xxz.reggie.entity.Employee;
import com.xxz.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.PushBuilder;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * @author xzxie
 * @create 2022/11/16 19:32
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request  登录请求
     * @param employee 前端返回的员工数据
     * @return 请求处理结果
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 将页面提交的明文密码进行 MD5 加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 根据用户提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 如果没有查询到返回登录失败结果
        if (emp == null) {
            return R.error("登陆失败");
        }

        // 密码比对，如果不一致，返回登陆失败结果
        if (!emp.getPassword().equals((password))) {
            return R.error("登陆失败");
        }

        // 查看员工状态，如果已被禁用返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        // 登录成功，将员工 id 存入 session，并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    /**
     * 员工推出
     *
     * @param request 退出请求
     * @return 请求处理结果
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清理 session 中保存的当前员工的id
        request.getSession().removeAttribute("employee");
        return R.success("推出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息为：{}", employee.toString());
        // 设置初始密码：123456，需要进行 MD5 加密后
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());

        // 获取当前登录用户的 id
        // Long employeeId = (Long) request.getSession().getAttribute("employee");
        // employee.setCreateUser(employeeId);
        // employee.setUpdateUser(employeeId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page 查询第几页
     * @param pageSize 查询每页的条数
     * @param name 姓名筛选
     * @return 查询结果
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {

        log.info("page = {}, pageSize = {}， name = {}", page, pageSize, name);

        // 分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        // 条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 添加条件构造器
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee :: getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee :: getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据 id 修改员工信息
     * @param employee 前端请求的待更新员工信息
     * @return 更新结果代码
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());

        long id = Thread.currentThread().getId();
        log.info("Controller--线程 id 为：{}", id);

        // Long empId = (Long) request.getSession().getAttribute("employee");
        // employee.setUpdateTime(LocalDateTime.now());
        // employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据 id 查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }


}
