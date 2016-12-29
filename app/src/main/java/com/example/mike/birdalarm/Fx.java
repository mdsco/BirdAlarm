package com.example.mike.birdalarm;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


public class Fx {

    public static void toggleContents(Context context, View alarmListItemContents){

        if(alarmListItemContents.isShown()){

            slide_up(context, alarmListItemContents);
            alarmListItemContents.setVisibility(View.GONE);

        } else {

            alarmListItemContents.setVisibility(View.VISIBLE);
            slide_down(context, alarmListItemContents);

        }
    }

    public static void slide_down(Context ctx, View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);

        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context ctx, View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);

        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

}
