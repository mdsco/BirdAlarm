package com.example.mike.birdalarm;

import android.content.Context;

public class InfoDialogResourceProvider {

    public static String getTextBasedOnType(Context context, String infoType) {

        switch(infoType){

            case "small_robin_chirping.mp4":
                return context.getString(R.string.small_robin_chirping);
            case "bower_bird4.mp4":
                return "It's a bower bird";
            default:
                return "No";

        }
    }

    public static int getImageBasedOnType(String infoType){

        switch (infoType){

            case "small_robin_chirping.mp4":
                return R.drawable.robin;
            default:
                return -1;
        }

    }
}
