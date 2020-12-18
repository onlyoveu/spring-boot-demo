package com.re.spring.boot.springbootdemo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static final String PATTERN_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_2 = "yyyy-MM-dd HH:mm:ss.n";

    /**
     * 2020-12-19 12:33:44.5
     * to
     * 2020-12-19 12:33:44
     *
     * @param datetime str
     * @return str
     */
    public static String toPATTERN_1(String datetime) {
        return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern(PATTERN_2)).format(DateTimeFormatter.ofPattern(PATTERN_1));
    }
}
