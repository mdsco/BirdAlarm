package com.example.mike.birdalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;

public class AlarmLockScreenTextureViewVideoActivity extends Activity
        implements TextureView.SurfaceTextureListener {

    private static final String LOG_TAG =
                AlarmLockScreenTextureViewVideoActivity.class.getName();

    private static final String FILE_NAME = "bower_bird4.mp4";

    private MediaPlayer mMediaPlayer;
    private AlarmManager alarmManager;
    private PendingIntent pendingAlarmIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.alarm_texture_layout);

        Intent intent = getIntent();
        Alarm alarm = (Alarm) intent.getExtras().getParcelable("alarmPassedInThroughIntent");

        String labelText = alarm.getLabel();

        long timestamp = alarm.getId();
        Log.v(LOG_TAG, "Words?: " + alarm.getHour()
                        + ":" + alarm.getMinute() + "Label?: " + labelText);

        TextView timeTextView = (TextView) findViewById(R.id.alarmTimeTextView);

        String timeFromTimestamp =
                Utility.getHourFromTimeStamp(timestamp)
                + ":" + Utility.getMinuteFromTimeStamp(timestamp);

        timeTextView.setText(timeFromTimestamp);

        TextView alarmLabel = (TextView) findViewById(R.id.label_textview);
        alarmLabel.setText(labelText);

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(stopAlarm());

        Button sleepButton = (Button) findViewById(R.id.sleep_button);
        sleepButton.setOnClickListener(getSleepOnClickListener(alarm));

        initView();
    }

    @NonNull
    private View.OnClickListener getSleepOnClickListener(final Alarm alarm) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String snoozeInterval =
                        PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                        .getString(getString(R.string.pref_snooze_key),
                                getString(R.string.pref_snooze_default));

                int hour = alarm.getHour();
                int minute = alarm.getMinute() + Integer.valueOf(snoozeInterval);
                int id = (int) System.currentTimeMillis();

                Alarm pauseAlarm = new Alarm(getBaseContext(), hour, minute, id);

                finish();

                registerAlarm(getBaseContext(), pauseAlarm);

            }
        };
    }

    private void registerAlarm(Context context, Alarm alarm) {

        int hour = alarm.getHour() + 12;
        int minute = alarm.getMinute();
        long id = alarm.getId();

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        alarmIntent.putExtra("alarmPassedInThroughIntent", alarm);

        alarmIntent.putExtra("Time", hour + ":" +
                String.format("%02d", minute));

        pendingAlarmIntent = PendingIntent.getBroadcast(context, (int) id, alarmIntent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), pendingAlarmIntent);

    }

    @NonNull
    private View.OnClickListener stopAlarm() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        };
    }

    private void initView() {
        TextureView textureView = (TextureView) findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        textureView.startAnimation(animation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {

        Surface surface = new Surface(surfaceTexture);

        try {
            AssetFileDescriptor afd = getAssets().openFd(FILE_NAME);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(),
                                                afd.getStartOffset(), afd.getLength());
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepareAsync();

            // Play video when the media source is ready for playback.
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

        } catch (IllegalArgumentException e) {
            Log.d(LOG_TAG, e.getMessage());
        } catch (SecurityException e) {
            Log.d(LOG_TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.d(LOG_TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }
}