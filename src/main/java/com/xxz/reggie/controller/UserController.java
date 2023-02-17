package com.xxz.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxz.reggie.common.R;
import com.xxz.reggie.entity.User;
import com.xxz.reggie.service.UserService;
import com.xxz.reggie.utils.SMSUtils;
import com.xxz.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author xzxie
 * @create 2022/12/27 10:29
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            // 生成随机的 4 位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("code = {}", code);

            // 调用阿里云提供的短信服务 API 完成发送短信
            // SMSUtils.sendMessage("瑞吉外卖", "", phone, code);

            session.setAttribute(phone, code);

            return R.success("手机验证码短信发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session) {

        log.info(map.toString());

        // 获取手机号
        String phone = map.get("phone");

        // 获取验证码
        String code = map.get("code");

        // 从 Session 中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

        // 进行验证码比对
        if (codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper);
            if (user == null) {
                // 判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());

            return R.success(user);
        }

        return R.error("登录失败");
    }
}
