package com.example.mike.birdalarm;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;


public class VideoAlertDialogFragment extends DialogFragment {


    private String LOG_TAG = VideoAlertDialogFragment.class.getSimpleName();

    VideoAlertDialog videoAlertDialog;

    public interface VideoAlertDialogListener {
        void onDialogPositiveClick();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        int viewPosition = getActivity().getIntent().getIntExtra("viewPosition", -1);
        videoAlertDialog = new VideoAlertDialog(activity, viewPosition);

        String filename = getArguments().getString("filename");
        videoAlertDialog.setFileName(filename);
        return videoAlertDialog.getAlertDialog();

    }

}
