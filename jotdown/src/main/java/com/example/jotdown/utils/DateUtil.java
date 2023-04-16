package com.example.jotdown.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getNowDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public static String getNowDateTime(String format){
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static String getNowDateTime(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public static long timeToTimestamp(String format,String strTime){
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        long timestamp=0;
        try {
            Date date=sdf.parse(strTime);
            timestamp=date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }
}