package com.lxgy.analysis.transformer.utils;

import com.alibaba.fastjson.JSON;

import static org.junit.Assert.*;

/**
 * @author Gryant
 */
public class UserAgentUtilTest {

    public static void main(String[] args) {

        String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3514.0 Safari/537.36";
        userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134";


        UserAgentUtil.UserAgentInfo userAgentInfo = UserAgentUtil.analyticUserAgent(userAgent);

        System.out.println(JSON.toJSONString(userAgentInfo));
    }
}