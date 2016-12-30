package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.InputStream;


/**
 * REQUIRE VIDEOS TO BE IN 'raw' RESOURCE FOLDER!!!!!!!! (instead of 'assets' folder)
 */

public class AlarmLockScreenVideoActivity extends Activity {

    private VideoView videoView;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alarm_lock_screen_video);

        String time = getIntent().getStringExtra("Time");

        TextView alarmTimeTextView = (TextView) findViewById(R.id.alarm_time_text_view);
        alarmTimeTextView.setText(time);

        videoView = (VideoView) findViewById(R.id.alarm_video_view);

        try {
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.robin_chirping));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                videoView.seekTo(position);
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                position = 0;
                videoView.seekTo(position);
                videoView.start();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putInt("position", videoView.getCurrentPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        position = savedInstanceState.getInt("position");
    }
}