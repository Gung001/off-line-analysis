package com.lxgy.analysis.transformer.utils;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gryant
 */
public class DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_M_D_FORMAT = "MM/dd";

    /**
     * 将时间戳转换成为时间字符串（yyyy-MM-dd）
     *
     * @param input
     * @return
     */
    public static String parseLong2YYYYMMDD(long input) {
        return parseLong2String(input, DATE_FORMAT);
    }

    /**
     * 将时间戳转换为指定格式的字符串时间
     *
     * @param input
     * @param pattern
     * @return
     */
    public static String parseLong2String(long input, String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(input);
        return new SimpleDateFormat(pattern).format(calendar.getTime());
    }

    /**
     * 将时间字符串转换为时间戳
     *
     * @param input
     * @return
     */
    public static long parseString2Long(String input) {
        return parseString2Long(input, DATE_FORMAT);
    }

    /**
     * 将指定格式的数据转换为时间戳
     *
     * @param input
     * @param pattern
     * @return
     */
    public static long parseString2Long(String input, String pattern) {

        Date date = null;

        try {
            date = new SimpleDateFormat(pattern).parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date.getTime();
    }

    /**
     * 获取昨天的日期格式字符串数据
     *
     * @return
     */
    public static String getYesterday() {
        return getYesterday(DATE_FORMAT);
    }

    /**
     * 根据指定的时间格式获取昨天的日期格式字符串数据
     *
     * @return
     */
    public static String getYesterday(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return sdf.format(calendar.getTime());
    }

    /**
     * 判断输入的参数是否时一个有效的时间格式数据
     *
     * @param input
     * @return
     */
    public static boolean isValidateRunningDate(String input) {
        boolean result = false;

        if (StringUtils.isBlank(input)) {
            return result;
        }

        String regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher != null) {
            result = matcher.matches();
        }

        return result;
    }

    /**
     * 将nginx 服务器时间转换成为时间戳，如果发生错误||null，返回-1
     *
     * @param input
     * @return
     */
    public static long parseNginxServerTime2Long(String input) {

        if (StringUtils.isBlank(input)) {
            return -1;
        }

        Date date = parseNginxServerTime2Date(input);

        return date == null ? -1L : date.getTime();
    }

    /**
     * 将nginx 服务器时间转换成为date 对象，如果解析失败||null，返回null
     *
     * @param input 格式：1449410796.976
     * @return
     */
    public static Date parseNginxServerTime2Date(String input) {

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
