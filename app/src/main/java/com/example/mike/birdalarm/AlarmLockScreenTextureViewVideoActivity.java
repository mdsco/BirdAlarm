package com.example.mike.birdalarm;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class AlarmLockScreenTextureViewVideoActivity extends FragmentActivity
        implements TextureView.SurfaceTextureListener, AlarmInfoFragment.DialogClosedListener {

    private static final String LOG_TAG =
            AlarmLockScreenTextureViewVideoActivity.class.getName();

    private String FILE_NAME;
    private MediaPlayer mMediaPlayer;
    private boolean vibrate = false;
    private Vibrator vibrator;
    private int position = -1;
    private boolean isPlaying = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        if (savedInstanceState != null) {
            int savedPostion = savedInstanceState.getInt("position", -1);
            boolean savedPlayingState = savedInstanceState.getBoolean("playing", true);

            if (savedPostion != -1) {
                this.position = savedPostion;
            }
            if (!savedPlayingState) {
                this.isPlaying = savedPlayingState;
            }
        }

        setContentView(R.layout.alarm_texture_layout);

        Intent intent = getIntent();
        Alarm alarm = intent.getExtras().getParcelable("alarmPassedInThroughIntent");

        if (alarm != null) {
            this.vibrate = alarm.getVibrate();

            FILE_NAME = alarm.getAlarmType();

            long timestamp = alarm.getTimestamp();

            setLayoutViews(alarm, timestamp);

            if (this.vibrate) {
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                VibrationUtility.initiateVibration(vibrator);
            }
            createAndStartTextureViewAnimation();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMediaPlayer != null) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            boolean playing = mMediaPlayer.isPlaying();

            outState.putInt("position", currentPosition);
            outState.putBoolean("playing", playing);

            Log.v(LOG_TAG, "Is playing: " + mMediaPlayer.isPlaying());

        }
    }

    private void setLayoutViews(final Alarm alarm, long timestamp) {

        TextView timeTextView = (TextView) findViewById(R.id.alarmTimeTextView);
        String formattedTime = Utility.getFormattedTime(timestamp);
        timeTextView.setText(formattedTime);

        TextView amPmView = (TextView) findViewById(R.id.am_pm_textview);
        amPmView.setText(Utility.getAmOrPm(timestamp));

        TextView alarmLabel = (TextView) findViewById(R.id.label_textview);
        String labelText = alarm.getLabel();
        alarmLabel.setText(labelText);

        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(getStopAlarmOnClickListener(alarm));

        final Button sleepButton = (Button) findViewById(R.id.sleep_button);
        sleepButton.setOnClickListener(getSnoozeAlarmOnClickListener(alarm));

        final ImageButton infoButton = (ImageButton) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(getInfoButtonClickListener(alarm));
    }

    @NonNull
    private View.OnClickListener getStopAlarmOnClickListener(final Alarm alarm) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AlarmLockScreenTextureViewVideoActivity.this.vibrate) {
                    VibrationUtility.cancelVibration(vibrator);
                }

                GlobalState applicationContext = (GlobalState) getApplicationContext();
                ArrayList<Alarm> alarmList = applicationContext.getAlarmList();

                Alarm originalAlarm = getOriginalAlarm(alarmList, alarm);

                originalAlarm.setTimestampBasedOnNextViableDay();

                finish();

            }
        };
    }

    @NonNull
    private View.OnClickListener getSnoozeAlarmOnClickListener(final Alarm alarm) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AlarmLockScreenTextureViewVideoActivity.this.vibrate) {
                    VibrationUtility.cancelVibration(vibrator);
                }

                GlobalState applicationContext = (GlobalState) getApplicationContext();
                ArrayList<Alarm> alarmList = applicationContext.getAlarmList();

                Alarm originalAlarm = getOriginalAlarm(alarmList, alarm);

                new SnoozeAlarm(AlarmLockScreenTextureViewVideoActivity.this, originalAlarm);

                finish();
            }
        };
    }

    @NonNull
    private View.OnClickListener getInfoButtonClickListener(final Alarm alarm) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mMediaPlayer.pause();
                Log.v(LOG_TAG, "Is playing: " + mMediaPlayer.isPlaying());
                AlarmInfoFragment infoFragment = AlarmInfoFragment.newInstance(alarm.getAlarmType());
                infoFragment.show(getSupportFragmentManager(), "dialog");

            }

        };
    }

    private Alarm getOriginalAlarm(ArrayList<Alarm> alarmList, Alarm alarm) {

        Alarm originalAlarm = null;

        for (Alarm alarmInList : alarmList) {
            if (alarmInList.getId() == alarm.getId()) {
                originalAlarm = (Alarm) alarmInList;
            }
        }

        return originalAlarm;
    }

    private void createAndStartTextureViewAnimation() {

        TextureView textureView = (TextureView) findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
        if (position == -1) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            textureView.startAnimation(animation);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                          int surfaceWidth, int surfaceHeight) {


        Surface surface = new Surface(surfaceTexture);

        try {

            AssetFileDescriptor birdFileDiscriptor = getAssets().openFd(FILE_NAME);

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(birdFileDiscriptor.getFileDescriptor(),
                    birdFileDiscriptor.getStartOffset(), birdFileDiscriptor.getLength());
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepareAsync();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    if (position != -1) {
                        mediaPlayer.seekTo(position);
                    }
                    mediaPlayer.start();
                    if (!isPlaying) {
                        mediaPlayer.pause();
                    }
                }
            });

        } catch (IllegalArgumentException e) {
            Log.d(LOG_TAG, e.getMessage());
        } catch (SecurityException e) {
//            Log.d(LOG_TAG, e.getMessage());
        } catch (IllegalStateException e) {
//            Log.d(LOG_TAG, e.getMessage());
        } catch (IOException e) {
//            Log.d(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                            int surfaceWidth, int surfaceHeight) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }


    @Override
    public void onDialogClosed() {

        mMediaPlayer.start();
        Log.v(LOG_TAG, "Is playing: " + mMediaPlayer.isPlaying());

    }
}