package com.example.gymapp;

public class ExerciseTracking {
    private String exname;
    private String eximg;

    public ExerciseTracking(String exname, String eximg) {
        this.exname = exname;
        this.eximg = eximg;
    }

    public String getExname() {
        return exname;
    }

    public String getEximg() {
        return eximg;
    }
}
