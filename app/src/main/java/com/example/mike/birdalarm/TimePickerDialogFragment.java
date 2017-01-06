package com.example.mike.birdalarm;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import java.util.Calendar;

public class TimePickerDialogFragment extends DialogFragment {


    private String LOG_TAG = TimePickerDialogFragment.class.getSimpleName();

    TimePickerDialog.OnTimeSetListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try  {
            mCallback = (TimePickerDialog.OnTimeSetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement OnTimeSetListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getContext(),
                mCallback, hour, minute,
                DateFormat.is24HourFormat(getContext()));

    }
}