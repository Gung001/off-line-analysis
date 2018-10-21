package com.lxgy.analysis.transformer.utils;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * @author Gryant
 */
public class LoggerUtilTest {

    public static void main(String[] args) {

        String logText = "192.168.30.1^A1539513524.462^Adata01^A/BfImg.gif?ver=1&u_mid=member002&c_time=1539523453954&en=e_cr&oid=orderId002&sdk=jdk&pl=java_server";
        Map<String, String> handleLog = LoggerUtil.handleLog(logText);
        System.out.println(JSON.toJSONString(handleLog));
    }

}