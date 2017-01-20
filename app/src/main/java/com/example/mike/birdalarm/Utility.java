package com.example.mike.birdalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    static long getTimeStampFromHourAndMinute(int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();

    }

    static long getTimeStampFromDayHourAndMinute(int day, int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();

    }

    public static long getTimeStampFromWeekDayHourAndMinute(int week, int day, int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }

    static long getTimeStampFromDayOfMonthHourAndMinute(int day, int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();

    }

    static long getTimestampForAWeekFromCurrentTimestamp(long timestamp) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(timestamp);
        int dayInMonth = getDayInMonthFromTimeStampAsInt(timestamp);
        calendar.set(Calendar.DAY_OF_MONTH, dayInMonth + 7);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();

    }

    public static long getTimeStampForAlarmSleep(long timestamp) {

        int hourFromTimeStamp = getHourFromTimeStamp(timestamp);
        int minuteFromTimeStamp = getMinuteFromTimeStamp(timestamp);

        return getTimeStampFromHourAndMinute(hourFromTimeStamp, minuteFromTimeStamp + 2);
    }


    static long getTimeStampForAlarmSleep(Context context) {

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

    static int getDayOfYearFromTimeStamp(long timestamp) {

        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("D");
        dateFormat.setTimeZone(TimeZone.getDefault());
        String formattedDate = dateFormat.format(date);

        return Integer.valueOf(formattedDate);

    }

    static String getDayOfWeekFromTimeStampAsString(long timestamp) {

        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        dateFormat.setTimeZone(TimeZone.getDefault());

        return dateFormat.format(date);

    }

    static int getDayInMonthFromTimeStampAsInt(long timestamp) {

        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d");
        dateFormat.setTimeZone(TimeZone.getDefault());

        return Integer.valueOf(dateFormat.format(date));

    }

    static int getWeekFromTimeStampAsInt(long timestamp) {

        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("w");
        dateFormat.setTimeZone(TimeZone.getDefault());

        return Integer.valueOf(dateFormat.format(date));

    }

    static int getHourFromTimeStamp(long timestamp) {

        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("h");
        dateFormat.setTimeZone(TimeZone.getDefault());
        String formattedDate = dateFormat.format(date);

        return Integer.valueOf(formattedDate);

    }

    static int get24FormatHourFromTimeStamp(long timestamp) {

        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("H");
        dateFormat.setTimeZone(TimeZone.getDefault());
        String formattedDate = dateFormat.format(date);

        return Integer.valueOf(formattedDate);

    }

    static int getMinuteFromTimeStamp(long timestamp) {

        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm");
        TimeZone timeZone = TimeZone.getDefault();
        dateFormat.setTimeZone(timeZone);
        String formattedDate = dateFormat.format(date);

        return Integer.valueOf(formattedDate);

    }

    static String getFormattedTime(long timestamp) {

        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("w EEE h:mm");
        TimeZone timeZone = TimeZone.getDefault();
        dateFormat.setTimeZone(timeZone);

        return dateFormat.format(date);

    }

    public static int getHourFor12HourClock(int hour) {

        if (hour == 0) {
            return 12;
        } else if (hour > 12) {
            return hour - 12;
        }

        return hour;
    }

    public static int getHourFor24HourClock(int hour, boolean amPm) {

        if (!amPm) {
            return hour + 12;
        }

        return hour;
    }

    static boolean determineIfAmOrPm(long timestamp) {

        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("a");
        TimeZone timeZone = TimeZone.getDefault();
        simpleDateFormat.setTimeZone(timeZone);
        String formattedDate = simpleDateFormat.format(date);

        return formattedDate.equals("AM");

    }

    static String getAmOrPm(long timestamp) {

        if (determineIfAmOrPm(timestamp)) {
            return "AM";
        }

        return "PM";
    }

    static String getFormattedNameFromFilename(String fileName) {
        String strings = fileName.substring(0, fileName.indexOf('.'));
        return strings.replace('_', ' ');
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
