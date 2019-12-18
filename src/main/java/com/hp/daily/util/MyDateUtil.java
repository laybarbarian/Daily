package com.hp.daily.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDateUtil {
    private MyDateUtil() {
    }

    public static  Date getNowDateYYYYMMdd() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int year = calendar.get(Calendar.YEAR);
        // 获取月
        int month = calendar.get(Calendar.MONTH);
        // 获取日
        int date = calendar.get(Calendar.DATE);
        return sdf.parse(year + "-" + (month + 1) + "-" + date);
    }

    public  static  String getNowDateStr(String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }

    public static  Date getDateByPatternStr(String pattern,String string){
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            date = sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  date;
    }

    public static  String getDateStrByPatternDate(String pattern,Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return  sdf.format(date);
    }
}
