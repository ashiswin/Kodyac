package com.ashiswin.kodyac;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by ashis on 4/3/2018.
 */

public class Util {
    public static String prettyDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("MMM dd, yyyy");
        return format.format(newDate);
    }
}
