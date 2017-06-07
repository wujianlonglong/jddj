package com.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by gaoqichao on 15-11-5.
 */
public class DateUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 默认的日期格式
     */
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private DateUtil() {

    }

    /**
     * 将LocalDateTime对象转换为Date对象
     *
     * @param localDateTime 　LocalDateTime类型日期
     * @return　转换后的Date对象
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * 将Date对象转换为LocalDateTime对象
     *
     * @param date 日期
     * @return　LocalDateTime对象
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }


    /**
     * 将字符串转换为日期
     *
     * @param dateStr
     * @return
     */
    public static Date parse(String dateStr) {
        return parse(dateStr, DEFAULT_DATE_FORMAT);
    }

    /**
     * 将字符串转换为日期
     *
     * @param dateStr 日期字符串
     * @param pattern 　日期格式
     * @return　日期
     */
    public static Date parse(String dateStr, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            LOG.error("给定的日期不正确，日期:{dateStr},需要的格式为：{pattern}", dateStr, pattern);
        }
        return date;
    }

    /**
     * 日期格式话,默认格式
     *
     * @param date 日期
     * @return 日期字符串
     */
    public static String format(Date date) {
        if (date == null) {
            date = new Date();
        }
        return format(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * 按照指定格式将日期格式话
     *
     * @param date 日期
     * @return 日期字符串
     */
    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }
}
