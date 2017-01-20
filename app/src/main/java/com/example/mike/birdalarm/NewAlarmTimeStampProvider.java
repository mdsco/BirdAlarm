package com.example.mike.birdalarm;

import android.util.Log;

import static java.util.Calendar.*;

class NewAlarmTimeStampProvider {

    private static final String LOG_TAG = NewAlarmTimeStampProvider.class.getSimpleName();

    static long getNextAlarmTimestamp(long timestamp, Alarm.Days[] days, boolean repeating) {

        for (Alarm.Days day : days) {

            String dayName = day.getDayName();

            int dayIntForDayInList = getDayInt(dayName);
            int hour = Utility.get24FormatHourFromTimeStamp(timestamp);
            int minute = Utility.getMinuteFromTimeStamp(timestamp);

            long newTimestamp = Utility.getTimeStampFromDayHourAndMinute
                    (dayIntForDayInList, hour, minute);

            String formattedTime = Utility.getFormattedTime(newTimestamp);

            if (newTimestamp > System.currentTimeMillis()) {
//                Log.v(LOG_TAG, formattedTime + " is new timestamp");
                return newTimestamp;
            }

        }

        if (repeating) {

            if (days.length != 0) {

                String dayName = days[0].getDayName();
                int weekFromTimeStampAsInt = Utility.getWeekFromTimeStampAsInt(timestamp);

                int week = weekFromTimeStampAsInt;
                if (weekFromTimeStampAsInt <= Utility.getWeekFromTimeStampAsInt(System.currentTimeMillis())) {
                    week = weekFromTimeStampAsInt + 1;
                }

                int dayInt = getDayInt(dayName);
                int hour = Utility.get24FormatHourFromTimeStamp(timestamp);
                int minute = Utility.getMinuteFromTimeStamp(timestamp);

                long newTimestamp = Utility.getTimeStampFromWeekDayHourAndMinute(week, dayInt, hour, minute);

//                Log.v(LOG_TAG, Utility.getFormattedTime(newTimestamp)
//                        + " is new timestamp");
                return newTimestamp;

            }

        }

//        Log.v(LOG_TAG, "no new timestamp, -1 returned to indicate alarm cancel ");
        //if -1 is returned no alarm should be triggered; need to cancel any pending alarms for passedin timestamp
        return -1;

    }

    private static int getDayInt(String dayName) {

        switch (dayName) {
            case "Sunday":
                return SUNDAY;
            case "Monday":
                return MONDAY;
            case "Tuesday":
                return TUESDAY;
            case "Wednesday":
                return WEDNESDAY;
            case "Thursday":
                return THURSDAY;
            case "Friday":
                return FRIDAY;
            case "Saturday":
                return SATURDAY;
            default:
                return 0;

        }

    }
}
