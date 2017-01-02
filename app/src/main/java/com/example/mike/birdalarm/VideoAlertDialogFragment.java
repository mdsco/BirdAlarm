package com.example.mike.birdalarm;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;


public class VideoAlertDialogFragment extends DialogFragment {

    VideoAlertDialog videoAlertDialog;

    public interface VideoAlertDialogListener {
        void onDialogPositiveClick();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        videoAlertDialog = new VideoAlertDialog(activity);

        String filename = getArguments().getString("filename");
        videoAlertDialog.setFileName(filename);
        return videoAlertDialog.getAlertDialog();

    }

}
