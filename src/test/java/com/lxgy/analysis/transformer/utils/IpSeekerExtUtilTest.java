package com.lxgy.analysis.transformer.utils;

import com.alibaba.fastjson.JSON;

/**
 * @author Gryant
 */
public class IpSeekerExtUtilTest {

    public static void main(String[] args) {

        IpSeekerExtUtil seekerExtUtil =new IpSeekerExtUtil();
        IpSeekerExtUtil.RegionInfo regionInfo = seekerExtUtil.analyticIp("119.75.213.61");
//        IpSeekerExtUtil.RegionInfo regionInfo = seekerExtUtil.analyticIp("124.192.164.34");
//        IpSeekerExtUtil.RegionInfo regionInfo = seekerExtUtil.analyticIp("42.121.252.58");
        System.out.println(JSON.toJSONString(regionInfo));

    }
}