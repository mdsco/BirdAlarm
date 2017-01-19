package com.example.mike.birdalarm;

import android.util.Log;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;


 class NewAlarmTimeStampProvider {

    private static final String LOG_TAG = NewAlarmTimeStampProvider.class.getSimpleName();

     static long getTimestamp(long timestamp, Alarm.Days[] days, boolean repeating) {

        //this part works, I think
        if (days.length == 1 && repeating) {
            
            return Utility.getTimestampForAWeekFromCurrentTimestamp(timestamp);
            
        } else if (days.length > 1) {

            for (int indexOfDayInWeek = 0; indexOfDayInWeek < days.length; indexOfDayInWeek++) {

                //get day of week as string from timestamp
                String dayOfWeekFromTimeStamp =
                        Utility.getDayOfWeekFromTimeStampAsString(timestamp);

                int dayIntFromTimestamp = getDayInt(dayOfWeekFromTimeStamp);

                //get day of week from day name string as int
                String dayName = days[indexOfDayInWeek].getDayName();
                int dayIntForDayInList = getDayInt(dayName);

                //compare theses ints to make sure the one from the list is greater
                if (dayIntForDayInList > dayIntFromTimestamp) {

                    //if it is greater create a new timestamp by adding the difference
                    //between the daylist item int and the timestamp int
                    int hour = Utility.getHourFromTimeStamp(timestamp);
                    int minute = Utility.getMinuteFromTimeStamp(timestamp);

                    return Utility.getTimeStampFromDayHourAndMinute(dayIntForDayInList, hour, minute);

                }

            }
            
            if(repeating && days[days.length -1].getDayName().equals("Saturday")){

                String dayName = days[0].getDayName();

                int dayInt = getDayInt(dayName);

                int day = Utility.getDayInMonthFromTimeStampAsInt(timestamp);
                int hour = Utility.getHourFromTimeStamp(timestamp);
                int minute = Utility.getMinuteFromTimeStamp(timestamp);

                long timeStampFromDayHourAndMinute =
                        Utility.getTimeStampFromDayHourAndMinute(day + dayInt, hour, minute);

                Log.v(LOG_TAG, "New timestamp day as int "
                        + Utility.getDayInMonthFromTimeStampAsInt(timeStampFromDayHourAndMinute));
                Log.v(LOG_TAG, "New timestamp day string "
                        + Utility.getDayOfWeekFromTimeStampAsString(timeStampFromDayHourAndMinute));

            }

        } 

        return 0;

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
