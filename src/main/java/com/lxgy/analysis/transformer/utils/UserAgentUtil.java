package com.lxgy.analysis.transformer.utils;


import com.alibaba.fastjson.JSON;
import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import cz.mallat.uasparser.UserAgentInfo;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 解析浏览器的userAgent的工具类，内部就是调用uasparser jar 文件
 *
 * @author Gryant
 */
public class UserAgentUtil {

    static UASparser uaSparser;

    // static 代码块，初始化uasparser 对象
    static{
        try {
            uaSparser = new UASparser(OnlineUpdater.getVendoredInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析浏览器的userAgent 字符串，返回userAgentInfo对象<br/>
     * 如果userAgent 为空，返回null
     * @param userAgent
     * @return
     */
    public static UserAgentInfo analyticUserAgent(String userAgent){

        UserAgentInfo result = null;

        if (StringUtils.isEmpty(userAgent)) {
            return result;
        }

        try {

            cz.mallat.uasparser.UserAgentInfo info;

            // 解析
            info = uaSparser.parse(userAgent);

            result = new UserAgentInfo();
            result.setBrowserName(info.getUaFamily());
            result.setBrowserVersion(info.getBrowserVersionInfo());
            result.setOsName(info.getOsFamily());
            result.setOsVersion(info.getOsName());

            System.out.println("所有信息：\n" + JSON.toJSONString(info));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 内部解析后的浏览器的信息model
     */
    public static class UserAgentInfo{

        private String browserName;
        private String browserVersion;
        private String osName;
        private String osVersion;

        public UserAgentInfo(String browserName, String browserVersion, String osName, String osVersion) {
            this.browserName = browserName;
            this.browserVersion = browserVersion;
            this.osName = osName;
            this.osVersion = osVersion;
        }

        public UserAgentInfo() {
        }

        public String getBrowserName() {
            return browserName;
        }

        public void setBrowserName(String browserName) {
            this.browserName = browserName;
        }

        public String getBrowserVersion() {
            return browserVersion;
        }

        public void setBrowserVersion(String browserVersion) {
            this.browserVersion = browserVersion;
        }

        public String getOsName() {
            return osName;
        }

        public void setOsName(String osName) {
            this.osName = osName;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }
    }


}
