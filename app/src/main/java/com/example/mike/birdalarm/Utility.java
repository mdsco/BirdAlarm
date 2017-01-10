package com.example.mike.birdalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Utility {


    private static final String LOG_TAG = Utility.class.getSimpleName();

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


    public static long getTimeStampForAlarmSleep(Context context){

        SharedPreferences defaultSharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);

        final String snoozePreference = defaultSharedPreferences.getString(
                context.getString(R.string.pref_snooze_key),
                context.getString(R.string.pref_snooze_default));

        Integer snoozeInterval = Integer.valueOf(snoozePreference);

        long timestamp = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(timestamp);
        int minutes = Utility.getMinuteFromTimeStamp(timestamp);
        calendar.set(Calendar.MINUTE, minutes + snoozeInterval);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static int getDayOfYearFromTimeStamp(long timestamp){

        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("D");
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);
        int dayOfYear = Integer.valueOf(formattedDate);

        return dayOfYear;

    }

    public static int getHourFromTimeStamp(long timestamp){

        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("h");
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);
        int hour = Integer.valueOf(formattedDate);

        return hour;

    }

    public static int get24FormatHourFromTimeStamp(long timestamp){

        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("H");
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

    public static String getFormattedNameFromFilename(String fileName) {
        String strings = fileName.substring(0,fileName.indexOf('.'));
        return strings.replace('_',  ' ');
    }



/*
    This might be useful as a way to format time strings

    Scanner in = new Scanner(System.in);
    String time = in.next();
    in.close();

    DateFormat parseFormat = new SimpleDateFormat("h:mm:ssa");
    DateFormat displayFormat = new SimpleDateFormat("H:mm:ssa");

    try {

        Date date = parseFormat.parse(time);
        String timeIn24HourFormat = displayFormat.format(date);
        System.out.println(timeIn24HourFormat);

    } catch (ParseException e) {
        e.printStackTrace();
    }
*/



}
