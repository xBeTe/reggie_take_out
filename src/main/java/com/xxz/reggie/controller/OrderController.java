package com.xxz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxz.reggie.common.BaseContext;
import com.xxz.reggie.common.R;
import com.xxz.reggie.dto.OrdersDto;
import com.xxz.reggie.entity.AddressBook;
import com.xxz.reggie.entity.OrderDetail;
import com.xxz.reggie.entity.Orders;
import com.xxz.reggie.entity.User;
import com.xxz.reggie.service.AddressBookService;
import com.xxz.reggie.service.OrderDetailService;
import com.xxz.reggie.service.OrderService;
import com.xxz.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xzxie
 * @create 2023/2/17 16:13
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userpage(int page, int pageSize) {
        log.info("page = {}, pageSize = {}", page, pageSize);

        // 分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        // 条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        // 根据用户查询
        Long userId = BaseContext.getCurrentId();
        queryWrapper.eq(Orders::getUserId, userId);
        // 查询结果按下单时间降序排列
        queryWrapper.orderByDesc(Orders::getOrderTime);
        // 执行分页查询
        orderService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");

        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> ordersDtos = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(item, ordersDto);

            // orderDetails
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(wrapper);

            ordersDto.setOrderDetails(orderDetails);

            return ordersDto;

        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtos);

        return R.success(ordersDtoPage);
    }
}
