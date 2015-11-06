package com.example.jianming.Utils;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Created by Jianming on 2015/10/29.
 */
public class TimeUtil {
    public static String getCurrentInFormatyyyyMMddHHmmss() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new GregorianCalendar().getTime());
    }

    public static String getGmtInFormatyyyyMMddHHmmss() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return sdf.format(new GregorianCalendar().getTime());
    }
}
