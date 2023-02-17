package com.xxz.reggie.common;

/**
 * 基于 ThreadLocal 封装工具类，用于保存和获取当前登录用户的 id
 * @author xzxie
 * @create 2022/11/21 16:27
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 保存当前用户的 id
     * @param id 当前用户的 id
     */
    public static void setCurrentId(long id) {
        threadLocal.set(id);
    }

    /**
     * 获取当前用户的 id
     * @return 返回档当前用户的 id
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }


}
