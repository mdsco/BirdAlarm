package com.example.mike.birdalarm;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

public class OnItemClickListenerListViewItem implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Context context = view.getContext();
        TextView alarmListItemContent = (TextView) view.findViewById(R.id.alarm_list_item_content);

        Fx.toggleContents(context, alarmListItemContent);

    }
}
