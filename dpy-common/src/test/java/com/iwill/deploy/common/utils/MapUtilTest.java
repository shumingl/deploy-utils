package com.iwill.deploy.common.utils;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class MapUtilTest {

    @Test
    public void of() {
        Map<String, Object> map01 = MapUtil.of(
                "key01", "value01",
                null, "value-null",
                "key03", "value03",
                "key04", null,
                null, null,
                "key05", "value05");
        System.out.println(map01);
    }
}