package com.example.mike.birdalarm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.AlarmViewHolder> {


    Context context;
    private List<Alarm> alarmList;

    interface Deleter {
        void deleteThisAlarm(Alarm alarm);
    }

    public AlarmRecyclerViewAdapter(Context context, List<Alarm> alarmList) {

        this.context = context;
        this.alarmList = alarmList;

    }

    @Override
    public AlarmViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View cardItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_card_item, parent, false);

        AlarmViewHolder alarmViewHolder = new AlarmViewHolder(cardItem);

        final AutoTransition transition = new AutoTransition();
        transition.setDuration(100);

        alarmViewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            boolean visible = false;

            @Override
            public void onClick(@NonNull View view) {

                Log.v("Initial elevation", view.getElevation() + "");
                Log.v("Initial z", view.getZ() + "");

                ConstraintLayout hiddenLayout = (ConstraintLayout) view.findViewById(R.id.card_options_layout);

                visible = !visible;
                float elevation = visible ? 10.0f : 3.0f;
                float zVal = visible ? 20.0f : 3.0f;

                TransitionManager.beginDelayedTransition(parent, transition);
                hiddenLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
                view.setElevation(elevation);
                view.setTranslationZ(zVal);

            }

        });

        return alarmViewHolder;
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        holder.alarmTime.setText(Utility.getFormattedTime(alarmList.get(position).getTimestamp()) + "");
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        TextView alarmTime;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            alarmTime = (TextView) itemView.findViewById(R.id.alarm_time_text_view);
        }
    }

}

