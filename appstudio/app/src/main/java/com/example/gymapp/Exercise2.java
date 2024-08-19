package com.example.gymapp;

public class Exercise2 {
    private String exName;
    private String exDesc;
    private String imageUrl;


    public Exercise2(String exName, String exDesc, String imageUrl) {
        this.exName = exName;
        this.exDesc = exDesc;
        this.imageUrl = imageUrl;

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


}
