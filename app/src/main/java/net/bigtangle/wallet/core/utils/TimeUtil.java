package net.bigtangle.wallet.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    private static final String format = "yyyy-MM-dd HH:mm:ss";

    public static long getTime(String dataStr) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date date = simpleDateFormat.parse(dataStr);
            return date.getTime();
        } catch (Exception e) {
            System.out.print("Time convert fail !" + dataStr);
        }
        return System.currentTimeMillis();
    }
}
