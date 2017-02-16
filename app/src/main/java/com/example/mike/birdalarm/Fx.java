package com.example.mike.birdalarm;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


public class Fx {


    private String LOG_TAG = Fx.class.getSimpleName();

    public static void toggleContents(Context context, View alarmListItemContents, View parent){

        if(alarmListItemContents.isShown()){

            slide_up(context, alarmListItemContents);
//            slide_up_parent(context, parent);
//            alarmListItemContents.setVisibility(View.GONE);

        } else {

//            alarmListItemContents.setVisibility(View.VISIBLE);
            slide_down(context, alarmListItemContents);
//            slide_down_parent(context, parent);

        }
    }

    public static void slide_down(Context ctx, final View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);

        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        v.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }


    public static void slide_down_parent(Context ctx, final View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down_parent);

        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        v.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }


    public static void slide_up(Context ctx, final View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);

        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        v.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    public static void slide_up_parent(Context ctx, final View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up_parent);

        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        v.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

}
