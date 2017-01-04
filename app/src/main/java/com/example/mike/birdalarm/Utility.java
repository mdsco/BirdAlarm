package com.example.mike.birdalarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static long getTimeStampForAlarmSleep(long timestamp){

        int hourFromTimeStamp = getHourFromTimeStamp(timestamp);
        int minuteFromTimeStamp = getMinuteFromTimeStamp(timestamp);

        return getTimeStampFromHourAndMinute(hourFromTimeStamp, minuteFromTimeStamp + 2);
    }


    //Need to get the sleep interval pref from storedPrefs
    public static long getTimeStampForAlarmSleep(){

        int sleepInterval = 1;//replace with storedpref

        long timestamp = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(timestamp);
        int minutes = Utility.getMinuteFromTimeStamp(timestamp);
        calendar.set(Calendar.MINUTE, minutes + sleepInterval);
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

    public static String getFormattedTime(long timestamp){

        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm");
        TimeZone timeZone = TimeZone.getDefault();
        simpleDateFormat.setTimeZone(timeZone);
        String formattedTime = simpleDateFormat.format(date);

        return formattedTime;

    }

    public static int getHourFor12HourClock(int hour){

        if(hour == 0){
            return 12;
        } else if(hour > 12){
            return hour - 12;
        }

        return hour;
    }

    //boolean is true if pm
    public static int getHourFor24HourClock(int hour, boolean amPm){

        if(!amPm){
            return hour + 12;
        }

        return hour;
    }

    public static boolean determineIfAmOrPm(long timestamp){

        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("a");
        TimeZone timeZone = TimeZone.getDefault();
        simpleDateFormat.setTimeZone(timeZone);
        String formattedDate = simpleDateFormat.format(date);

        return formattedDate.equals("AM");

    }

    public static String getAmOrPm(long timestamp){

        if(determineIfAmOrPm(timestamp)){
            return "AM";
        }

        return "PM";
    }
}
