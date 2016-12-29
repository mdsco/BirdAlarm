package com.example.mike.birdalarm;


import android.os.SystemClock;
import android.util.Log;

import java.security.Timestamp;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Utility {

    public static long getTimeStampFromHourAndMinute(int hour, int minute){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();

    }

    public static int getHourFromTimeStamp(long timestamp){

        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("h");
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);
        int hour = Integer.valueOf(formattedDate);

        return hour;

    }

    public static int getMinuteFromTimeStamp(long timestamp){

        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("mm");
        TimeZone timeZone = TimeZone.getDefault();
        sdf.setTimeZone(timeZone);
        String formattedDate = sdf.format(date);
        int minute = Integer.valueOf(formattedDate);

        return minute;

    }

    public static int getHourFor12HourClock(int hour){

        if(hour == 0){
            return 12;
        } else if(hour > 12){
            return hour - 12;
        }

        return hour;
    }

    public static int getHourFor24HourClock(int hour, boolean amPm){

        if(amPm){
            return hour + 12;
        }

        return hour;
    }

    public static boolean determineIfAmOrPm(long timestamp){


        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("a");
        TimeZone timeZone = TimeZone.getDefault();
        sdf.setTimeZone(timeZone);
        String formattedDate = sdf.format(date);

        if(formattedDate.equals("AM")){
            return false;
        } else {
            return true;
        }
    }

}
