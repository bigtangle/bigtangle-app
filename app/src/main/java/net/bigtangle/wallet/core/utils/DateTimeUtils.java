package net.bigtangle.wallet.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

    private static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static Date parseDate(String dataStr) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS);
        Date date = simpleDateFormat.parse(dataStr);
        return date;
    }

    public static long toDateMillis(String dateStr) {
        try {
            Date date = parseDate(dateStr);
            return date.getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}
