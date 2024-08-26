package com.example.gymapp;

import java.io.Serializable;

public class Exercise2 implements Serializable {
    private String exName;
    private String exDesc;
    private String imageUrl;
    private String activity;

    public Exercise2(String exName, String exDesc, String imageUrl, String activity) {
        this.exName = exName;
        this.exDesc = exDesc;
        this.imageUrl = imageUrl;
        this.activity = activity;
    }

    public String getExName() {
        return exName;
    }

    public String getExDesc() {
        return exDesc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getActivity() {
        return activity;
    }
}

