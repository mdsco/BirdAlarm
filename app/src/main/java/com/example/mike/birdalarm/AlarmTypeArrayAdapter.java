package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    private ViewParent currentView;


    AlarmTypeArrayAdapter(Context context, int resource, List<String> listItems) {
        super(context, resource, listItems);

        this.context = context;
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.alarm_type_item, parent, false);
        }

        TextView listItemTextView = (TextView) convertView.findViewById(R.id.alarm_type_textview);

        final String fileName = listItems.get(position);
        listItemTextView.setText(getTitle(fileName));
        listItemTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                currentView = view.getParent();

                Bundle bundle = new Bundle();
                bundle.putString("filename", fileName);

                VideoAlertDialogFragment videoAlertDialogFragment = new VideoAlertDialogFragment();
                videoAlertDialogFragment.setArguments(bundle);
                videoAlertDialogFragment.show(((Activity)getContext()).getFragmentManager(), "alarmTypeDialog");

            }

        });

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
