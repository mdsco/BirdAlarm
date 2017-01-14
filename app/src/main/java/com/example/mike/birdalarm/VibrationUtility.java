package com.example.mike.birdalarm;

import android.os.Vibrator;

class VibrationUtility {

    public static final long[] VIBRATION_PATTERN = {0, 500, 50, 100, 50, 80, 2000};

    static void initiateVibration(Vibrator vibrator) {

        vibrator.vibrate(VIBRATION_PATTERN, 0);
    }

    static void cancelVibration(Vibrator vibrator){
        vibrator.cancel();
    }

    static long[] getVibrationPattern(boolean vibrate){

        if(vibrate){
            return VIBRATION_PATTERN;
        }

        return new long[]{};
    }

}
