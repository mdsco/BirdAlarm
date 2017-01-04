package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


class AlarmTypeArrayAdapter extends ArrayAdapter<String>
                        implements VideoAlertDialogFragment.VideoAlertDialogListener{

    private final List<String> listItems;
    private final Context context;
    public static final String LOG_TAG = AlarmTypeArrayAdapter.class.getSimpleName();
    private final String alarmTypeFromAlarm;
    private ViewParent currentView;


    AlarmTypeArrayAdapter(Context context, int resource, List<String> listItems) {
        super(context, resource, listItems);

        this.context = context;
        this.listItems = listItems;
        this.alarmTypeFromAlarm
                = getTitle(((Activity) this.context).getIntent().getStringExtra("alarmType"));
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.alarm_type_item, parent, false);
        }

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.alarm_type_checkbox);


        TextView listItemTextView = (TextView) convertView.findViewById(R.id.alarm_type_textview);

        final String fileName = listItems.get(position);
        String alarmName = getTitle(fileName);
        listItemTextView.setText(alarmName);
        listItemTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                currentView = view.getParent();

                Bundle bundle = new Bundle();
                bundle.putString("filename", fileName);

                VideoAlertDialogFragment videoAlertDialogFragment =
                                                new VideoAlertDialogFragment();
                videoAlertDialogFragment.setArguments(bundle);
                videoAlertDialogFragment.show(((Activity)getContext())
                                            .getFragmentManager(), "alarmTypeDialog");

            }

        });

        if(alarmName.equals(this.alarmTypeFromAlarm)){
            checkBox.setChecked(true);
        }

        return convertView;
    }

    private String getTitle(String fileName) {
        String strings = fileName.substring(0,fileName.indexOf('.'));
        return strings.replace('_', ' ');
    }

    @Override
    public void onDialogPositiveClick() {

        LinearLayout linearLayout = (LinearLayout) this.currentView;
        CheckBox checkBox = (CheckBox) linearLayout.findViewById(R.id.alarm_type_checkbox);
        checkBox.setChecked(true);
    }
}
