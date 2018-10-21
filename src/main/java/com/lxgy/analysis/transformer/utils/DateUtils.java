package com.lxgy.analysis.transformer.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Gryant
 */
public class DateUtils {

    /**
     * 将nginx 服务器时间转换成为时间戳，如果发生错误||null，返回-1
     * @param input
     * @return
     */
    public static long parseNginxServerTime2Long(String input){

        if (StringUtils.isBlank(input)) {
            return -1;
        }

        Date date = parseNginxServerTime2Date(input);

        return date == null ? -1L : date.getTime();
    }

    /**
     * 将nginx 服务器时间转换成为date 对象，如果解析失败||null，返回null
     * @param input 格式：1449410796.976
     * @return
     */
    public static Date parseNginxServerTime2Date(String input){

        Date result = null;

        try {

            long timestep = Double.valueOf(Double.parseDouble(input) * 1000).longValue();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestep);
            result = calendar.getTime();

        } catch (Exception e) {
            // do nothing
        }

        return result;
    }

}
