package com.xxz.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxz.reggie.entity.AddressBook;
import com.xxz.reggie.mapper.AddressBookMapper;
import com.xxz.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author xzxie
 * @create 2023/2/14 16:11
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
