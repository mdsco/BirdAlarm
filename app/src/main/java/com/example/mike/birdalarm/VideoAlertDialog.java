package com.example.mike.birdalarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ListAdapter;

import java.io.IOException;

class VideoAlertDialog implements TextureView.SurfaceTextureListener {

    private static final String LOG_TAG = VideoAlertDialog.class.getSimpleName();
    private final Context context;

    private String fileName;
    private MediaPlayer mMediaPlayer;
    private AssetFileDescriptor birdFileDiscriptor;

    VideoAlertDialog(final Context context){

        this.context = context;
        this.fileName = "bower_bird3.mp4";

    }

    AlertDialog getAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);
        alertDialogBuilder.setTitle(getTitle(fileName));

        alertDialogBuilder.setNegativeButton("Nope",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMediaPlayer.stop();
                    }
                });

        alertDialogBuilder.setPositiveButton("Select",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mMediaPlayer.stop();
                Activity context = (Activity) VideoAlertDialog.this.context;
                ListFragment fragment = (ListFragment) context.getFragmentManager()
                                            .findFragmentById(R.id.alarm_selection_list);
                ListAdapter listAdapter = fragment.getListAdapter();

                ((VideoAlertDialogFragment.VideoAlertDialogListener) listAdapter).onDialogPositiveClick();
                context.finish();

            }
        });

        LayoutInflater inflater = ((Activity) this.context).getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_video_layout, null);
        alertDialogBuilder.setView(dialogView);

        TextureView textureView = (TextureView) dialogView.findViewById(R.id.video_texture_view);
        textureView.setSurfaceTextureListener(this);
        return alertDialogBuilder.create();
    }


    private String getTitle(String fileName) {
        String strings = fileName.substring(0,fileName.indexOf('.'));
        return strings.replace('_',  ' ');
    }

    void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {

        Surface surface = new Surface(surfaceTexture);

        try {

            birdFileDiscriptor = context.getAssets().openFd(fileName);

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(birdFileDiscriptor.getFileDescriptor(),
                    birdFileDiscriptor.getStartOffset(), birdFileDiscriptor.getLength());
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepareAsync();

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
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {

        try {
            birdFileDiscriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {}
}
