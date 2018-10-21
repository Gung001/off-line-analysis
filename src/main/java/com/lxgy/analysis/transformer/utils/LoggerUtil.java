package com.lxgy.analysis.transformer.utils;

import com.lxgy.analysis.transformer.common.EventLogConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理日志数据工具类
 *
 * @author Gryant
 */
public class LoggerUtil {

    /**
     * log util
     */
    private static final Logger logger = Logger.getLogger(LoggerUtil.class);

    /**
     * ip parser
     */
    private static IpSeekerExtUtil ipSeekerExtUtil = new IpSeekerExtUtil();

    /**
     * 处理日志数据 logText ，返回处理结果Map集合
     * 如果logText 没有指定数据格式直接返回空集合
     *
     * @param logText
     * @return
     */
    public static Map<String, String> handleLog(String logText) throws RuntimeException {

        Map<String, String> clientInfo = null;

        try {

            if (StringUtils.isBlank(logText)) {
                throw new RuntimeException("log text exception");
            }

            String[] splits = logText.trim().split(EventLogConstants.LOG_SEPARTIOR);
            if (splits == null || splits.length != 4) {
                throw new RuntimeException("log length exception");
            }

            // 日志格式为：ip^A服务器时间^Ahost^A请求参数
            clientInfo = new HashMap<>();
            clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_IP, splits[0].trim());
            clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME, String.valueOf(DateUtils.parseNginxServerTime2Long(splits[1])));
            int index = splits[3].indexOf("?");
            if (index < 0) {
                throw new RuntimeException("log requestBody exception");
            }

            String requestBody = splits[3].substring(index + 1);
            // 处理请求参数
            handleRequestBody(requestBody, clientInfo);
            // 处理userAgent
            handleUserAgent(clientInfo);
            // 处理IP
            handleIp(clientInfo);

        } catch (Exception e) {
            logger.error("handleLog error", e);
        }

        return clientInfo;
    }

    /**
     * deal ip
     *
     * @param clientInfo
     */
    private static void handleIp(Map<String, String> clientInfo) {

        if (!clientInfo.containsKey(EventLogConstants.LOG_COLUMN_NAME_IP)) {
            logger.warn("clientInfo doesn't contain key of" + EventLogConstants.LOG_COLUMN_NAME_IP);
            return;
        }

        String ip = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_IP);
        IpSeekerExtUtil.RegionInfo regionInfo = ipSeekerExtUtil.analyticIp(ip);
        if (regionInfo == null) {
            logger.warn("analyticIp fail");
            return;
        }

        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_COUNTRY, regionInfo.getCountry());
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_PROVINCE, regionInfo.getProvince());
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_CITY, regionInfo.getCity());
    }

    /**
     * 处理userAgent
     *
     * @param clientInfo
     */
    private static void handleUserAgent(Map<String, String> clientInfo) {

        if (!clientInfo.containsKey(EventLogConstants.LOG_COLUMN_NAME_USER_AGENT)) {
            logger.warn("clientInfo doesn't contain key of" + EventLogConstants.LOG_COLUMN_NAME_USER_AGENT);
            return;
        }

        String userAgent = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_USER_AGENT);
        UserAgentUtil.UserAgentInfo userAgentInfo = UserAgentUtil.analyticUserAgent(userAgent);
        if (userAgentInfo == null) {
            logger.warn("analyticUserAgent fail");
            return;
        }

        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME, userAgentInfo.getBrowserName());
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION, userAgentInfo.getBrowserVersion());
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_OS_NAME, userAgentInfo.getOsName());
        clientInfo.put(EventLogConstants.LOG_COLUMN_NAME_OS_VERSION, userAgentInfo.getOsVersion());
    }

    /**
     * 处理请求参数
     */
    private static void handleRequestBody(String requestBody, Map<String, String> clientInfo) {

        if (StringUtils.isBlank(requestBody)) {
            logger.debug("requestBody is null");
            return;
        }

        String[] requestParams = requestBody.split("&");
        for (String requestParam : requestParams) {

            int index = requestParam.indexOf("=");
            if (index < 0) {
                logger.debug("param[" + requestParam + "] canot be parsed");
                continue;
            }

            String key = requestParam.substring(0, index);
            String value = null;

            try {

                value = URLDecoder.decode(requestParam.substring(index + 1), "UTF-8");
            } catch (Exception e) {
                logger.debug("param[" + requestParam + "] parses error");
                continue;
            }

            if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
                logger.debug("param[" + requestParam + "] parsed result is null");
                continue;
            }

            clientInfo.put(key, value);
        }
    }
}
