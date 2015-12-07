package com.josh.profrate.elements;

import java.util.Calendar;

public class TimeConverter {

    private static String[] month = new String[]{"Jan.", "Feb.", "Mar.", "Apr.", "May.", "Jun.",
                            "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."};

    public static String convertTime(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String yy = calendar.get(Calendar.YEAR) + "";
        String mm = month[calendar.get(Calendar.MONTH)];
        String dd = calendar.get(Calendar.DAY_OF_MONTH) + "";
        if(dd.length() == 1) dd = "0" + dd;
        String hh = calendar.get(Calendar.HOUR_OF_DAY)+"";
        if(hh.length() == 1) hh = "0" + hh;
        String min = calendar.get(Calendar.MINUTE)+"";
        if(min.length() == 1) min = "0" + min;
        return hh+":"+min+", "+mm+" "+dd+", "+yy;
    }

}
