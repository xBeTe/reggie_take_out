package com.xxz.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author xzxie
 * @create 2022/11/23 17:20
 */
@Slf4j
public class StringTest {

    @Test
    public void testSplit() {
        String ids = "123";
        String[] split = ids.split(",");
        log.info(Arrays.toString(split));
    }
}
