package com.example.jianming.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeUtil {
    public static String getCurrentInFormatyyyyMMddHHmmss() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new GregorianCalendar().getTime());
    }

    public static String currentFormatyyyyMMddHHmmss() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new Date());
    }
}
