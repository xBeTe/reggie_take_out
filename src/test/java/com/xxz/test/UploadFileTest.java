package com.xxz.test;

import org.junit.jupiter.api.Test;

/**
 * @author xzxie
 * @create 2022/11/22 16:23
 */
public class UploadFileTest {

    @Test
    public void testUpload() {
        String fileName = "hello.112.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);

    }

}
