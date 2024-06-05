package com.example.gymapp;

public class WeeklyGoal {
    private String day;
    private String focusGoal;

    public WeeklyGoal(String day, String focusGoal) {
        this.day = day;
        this.focusGoal = focusGoal;
    }

    public String getDay() {
        return day;
    }

    public String getFocusGoal() {
        return focusGoal;
    }
}
