package com.example.gymapp;

public class DayCount {
    private final String day;
    private final String count;

    public DayCount(String day, String count) {
        this.day = day;
        this.count = count;
    }

    public String getDay() {
        return day;
    }

    public String getCount() {
        return count;
    }
}