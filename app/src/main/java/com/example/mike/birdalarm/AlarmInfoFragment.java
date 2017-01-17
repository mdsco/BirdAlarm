package com.example.mike.birdalarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmInfoFragment extends DialogFragment {

    interface DialogClosedListener {

        void onDialogClosed();

    }

    private static final String LOG_TAG = AlarmInfoFragment.class.getSimpleName();


    static AlarmInfoFragment newInstance(String type) {
        AlarmInfoFragment alarmInfoFragment = new AlarmInfoFragment();
        Bundle args = new Bundle();
        args.putString("info_type", type);
        alarmInfoFragment.setArguments(args);

        return alarmInfoFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog alertDialog = getAlertDialog();

        return alertDialog;
    }

    @NonNull
    private AlertDialog getAlertDialog() {
        String infoType = (String) getArguments().get("info_type");

        String dialogTitleText = Utility.getFormattedNameFromFilename(infoType);
        int imageBasedOnType = InfoDialogResourceProvider.getImageBasedOnType(infoType);
        String infoText = InfoDialogResourceProvider.getTextBasedOnType(getActivity(), infoType);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.info_layout, null);

        TextView dialogTitle = (TextView) view.findViewById(R.id.info_title_textview);
        dialogTitle.setText(dialogTitleText);

        ImageView dialogImage = (ImageView) view.findViewById(R.id.info_image_view);
        dialogImage.setImageResource(imageBasedOnType);

        TextView dialogTextView = (TextView) view.findViewById(R.id.info_dialog_text);

        dialogTextView.setText(infoText);

        builder.setView(view)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        notifyParentOfDialogClose();
                    }
                });


        AlertDialog alertDialog = builder.create();
        int color = Color.argb(200, 0, 0, 0);
        final Drawable drawable = new ColorDrawable(color);

        alertDialog.getWindow().setBackgroundDrawable(drawable);
        return alertDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        notifyParentOfDialogClose();
    }

    private void notifyParentOfDialogClose() {
        DialogClosedListener dialogClosedListener = (DialogClosedListener) getActivity();
        dialogClosedListener.onDialogClosed();
    }

}