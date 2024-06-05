package com.example.gymapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

public class Day_Adapter extends ArrayAdapter<WeeklyGoal> {
    private Context mContext;
    private List<WeeklyGoal> weeklyGoalsList;
    private Calendar calendar;

    public Day_Adapter(Context context, List<WeeklyGoal> weeklyGoals) {
        super(context, 0, weeklyGoals);
        this.mContext = context;
        this.weeklyGoalsList = weeklyGoals;
        this.calendar = Calendar.getInstance();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final WeeklyGoal goal = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_days, parent, false);
        }

        TextView textViewListItem = convertView.findViewById(R.id.textViewListItem);
        ImageView lockIcon = convertView.findViewById(R.id.lockIcon);
        View clickOverlay = convertView.findViewById(R.id.clickOverlay);

        textViewListItem.setText(goal.getDay() + ": " + goal.getFocusGoal());

        if (isToday(goal.getDay())) {
            lockIcon.setVisibility(View.GONE);
            clickOverlay.setVisibility(View.GONE);
            convertView.setClickable(true);
        } else {
            lockIcon.setVisibility(View.VISIBLE);
            clickOverlay.setVisibility(View.VISIBLE);
            convertView.setClickable(false);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isToday(goal.getDay())) {
                    Intent intent = new Intent(getContext(), exfocus_list.class);
                    intent.putExtra("day", goal.getDay());
                    intent.putExtra("focusbody", goal.getFocusGoal());
                    getContext().startActivity(intent);
                } else {
                    Toast.makeText(mContext, "This day is locked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    private boolean isToday(String day) {
        Calendar todayCalendar = Calendar.getInstance();
        int dayOfWeek = todayCalendar.get(Calendar.DAY_OF_WEEK);

        String today = convertDayOfWeek(dayOfWeek);

        return day.equalsIgnoreCase(today);
    }

    private String convertDayOfWeek(int dayOfWeek) {
        String[] daysOfWeek = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return daysOfWeek[dayOfWeek];
    }

}

