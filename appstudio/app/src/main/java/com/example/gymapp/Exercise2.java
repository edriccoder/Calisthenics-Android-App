package com.example.gymapp;

import java.io.Serializable;

public class Exercise2 implements Serializable {
    private String exName;
    private String exDesc;
    private String imageUrl;
    private String activity;
    private String localImagePath;  // Store local image path
    private String otherFocus;

    // Constructor with parameters
    public Exercise2(String exName, String exDesc, String imageUrl, String activity, String otherFocus) {
        this.exName = exName;
        this.exDesc = exDesc;
        this.imageUrl = imageUrl;
        this.activity = activity;
        this.otherFocus = otherFocus;
    }

    // Getters and setters...
    public String getExName() {
        return exName;
    }

    public void setExName(String exName) {
        this.exName = exName;
    }

    public String getExDesc() {
        return exDesc;
    }

    public void setExDesc(String exDesc) {
        this.exDesc = exDesc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getOtherFocus() {
        return otherFocus;
    }

    public void setOtherFocus(String otherFocus) {
        this.otherFocus = otherFocus;
    }

    public String getLocalImagePath() {
        return localImagePath;
    }

    public void setLocalImagePath(String localImagePath) {
        this.localImagePath = localImagePath;
    }
}
