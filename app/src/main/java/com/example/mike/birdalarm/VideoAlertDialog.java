package com.example.mike.birdalarm;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Toast;

import java.io.IOException;

public class VideoAlertDialog implements TextureView.SurfaceTextureListener {

    private static final String LOG_TAG = VideoAlertDialog.class.getSimpleName();
    private final Context context;
    MediaPlayer mMediaPlayer;

    public VideoAlertDialog(final Context context){

        this.context = context;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);
        alertDialogBuilder.setTitle("Robin");

        alertDialogBuilder.setPositiveButton("yes",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Toast.makeText(context, "You clicked yes"
                            + " button",Toast.LENGTH_LONG).show();

                    mMediaPlayer.stop();

                }
            });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) context).finish();
            }
        });

        LayoutInflater inflater = ((Activity) this.context).getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_video_layout, null);
        alertDialogBuilder.setView(dialogView);

        TextureView textureView = (TextureView) dialogView.findViewById(R.id.video_texture_view);
        textureView.setSurfaceTextureListener(this);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

        Surface surface = new Surface(surfaceTexture);

        try {

            AssetFileDescriptor birdFileDiscriptor =
                    context.getAssets().openFd("robin_chirping.mp4");

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
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
