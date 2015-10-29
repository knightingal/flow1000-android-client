package com.example.jianming.Utils;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by Jianming on 2015/10/29.
 */
public class TimeUtil {
    public static String getCurrentInFormatyyyyMMddHHmmss() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new GregorianCalendar().getTime());
    }
}
