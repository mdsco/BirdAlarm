package com.example.mike.birdalarm;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class AlarmTypeArrayAdapter extends ArrayAdapter<String> {

    private final List<String> listItems;
    private final Context context;
    public static final String LOG_TAG = AlarmTypeArrayAdapter.class.getSimpleName();


    public AlarmTypeArrayAdapter(Context context, int resource, List<String> listItems) {
        super(context, resource, listItems);

        this.context = context;
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.alarm_type_item, parent, false);
        }

        TextView listItemTextView = (TextView) convertView.findViewById(R.id.alarm_type_textview);

        listItemTextView.setText(listItems.get(position));
        listItemTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                new VideoAlertDialog(AlarmTypeArrayAdapter.this.context);

            }

        });

        return convertView;
    }
}