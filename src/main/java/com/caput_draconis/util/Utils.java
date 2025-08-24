package com.caput_draconis.util;

import lombok.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @NonNull
    public static String getCurrentDateTime() {
        return SIMPLE_DATE_FORMAT.format(new Date());
    }

    @NonNull
    public static Date convertStringToDate(@NonNull final String date){
        try {
            return SIMPLE_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return new Date();
    }
}
