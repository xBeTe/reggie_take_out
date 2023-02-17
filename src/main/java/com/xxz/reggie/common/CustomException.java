package com.xxz.reggie.common;

/**
 * 自定义业务异常类
 *
 * @author xzxie
 * @create 2022/11/21 20:05
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
