package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


class AlarmTypeArrayAdapter extends ArrayAdapter<String>
        implements VideoAlertDialogFragment.VideoAlertDialogListener {

    private final List<String> listItems;
    private final Context context;
    public static final String LOG_TAG = AlarmTypeArrayAdapter.class.getSimpleName();
    private final String alarmTypeFromAlarm;
    private ViewParent currentView;
    private AlarmTypeListFragment listFragment;

    AlarmTypeArrayAdapter(Context context, int resource, List<String> listItems) {
        super(context, resource, listItems);

        this.context = context;
        this.listItems = listItems;

        String alarmType = ((Activity) this.context).getIntent().getStringExtra("alarmType");

        this.alarmTypeFromAlarm = getTitle(alarmType);
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
        final String alarmName = getTitle(fileName);

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.alarm_type_checkbox);

        final int viewPosition = ((Activity) context).getIntent().getIntExtra("viewPosition", -1);

        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                currentView = view.getParent();
                checkCheckboxAssociatedWithAction();

                Intent intent = new Intent();
                intent.putExtra("alarmName", alarmName);
                intent.putExtra("viewPosition", viewPosition);

                Activity activity = ((Activity) context);
                activity.setResult(Activity.RESULT_OK, intent);

                activity.finish();
            }

        });

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
                videoAlertDialogFragment.show(((Activity) getContext())
                        .getFragmentManager(), "alarmTypeDialog");

            }

        });

        if (alarmName.equals(this.alarmTypeFromAlarm)) {
            checkBox.setChecked(true);
        }

        return convertView;
    }

    private String getTitle(String fileName) {
        String strings = fileName.substring(0, fileName.indexOf('.'));
        return strings.replace('_', ' ');
    }

    @Override
    public void onDialogPositiveClick() {
        checkCheckboxAssociatedWithAction();
    }

    private void checkCheckboxAssociatedWithAction() {
        clearCheckboxes();
        LinearLayout linearLayout = (LinearLayout) this.currentView;
        CheckBox checkBox = (CheckBox) linearLayout.findViewById(R.id.alarm_type_checkbox);
        checkBox.setChecked(true);
    }

    private void clearCheckboxes() {

        ListView listView = (ListView) this.listFragment.getView().findViewById(android.R.id.list);
        int count = listView.getCount();
        for (int i = 0; i < count; i++) {
            View child = listView.getChildAt(i);
            CheckBox checkBox = (CheckBox) child.findViewById(R.id.alarm_type_checkbox);
            checkBox.setChecked(false);
        }

    }

    void setList(AlarmTypeListFragment alarmTypeListFragment) {
        this.listFragment = alarmTypeListFragment;
    }
}