package com.odcloud.infrastructure.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter
        .ofPattern("yyyyMMdd");

    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatDateTime(LocalDateTime dateTime, String format) {
        return dateTime == null ? "" :
            dateTime.format(DateTimeFormatter.ofPattern(format, Locale.KOREA));
    }

    public static LocalDateTime parse(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, formatter);
    }
}
