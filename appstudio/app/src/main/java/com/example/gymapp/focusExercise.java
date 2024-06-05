package com.example.gymapp;

import android.os.Parcel;
import android.os.Parcelable;

public class focusExercise implements Parcelable {
    private String exerciseName;
    private String description;
    private String imageUrl;
    private String difficulty;
    private String activity;

    public focusExercise(String exerciseName, String description, String imageUrl, String difficulty, String activity) {
        this.exerciseName = exerciseName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.difficulty = difficulty;
        this.activity = activity;
    }

    protected focusExercise(Parcel in) {
        exerciseName = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        difficulty = in.readString();
        activity = in.readString();
    }

    public static final Creator<focusExercise> CREATOR = new Creator<focusExercise>() {
        @Override
        public focusExercise createFromParcel(Parcel in) {
            return new focusExercise(in);
        }

        @Override
        public focusExercise[] newArray(int size) {
            return new focusExercise[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(exerciseName);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(difficulty);
        dest.writeString(activity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getActivity() {
        return activity;
    }
}
