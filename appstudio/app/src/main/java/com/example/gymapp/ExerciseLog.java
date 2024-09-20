package com.example.gymapp;

public class ExerciseLog {
    private String exerciseName;
    private int sets;
    private int reps;
    private String logDate;

    public ExerciseLog(String exerciseName, int sets, int reps, String logDate) {
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.logDate = logDate;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public String getLogDate() {
        return logDate;
    }
}
