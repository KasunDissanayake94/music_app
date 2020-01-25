package com.example.myapplication.model;

import com.google.firebase.database.Exclude;

public class UploadSong {
    public String title;
    public String duration;
    public String link;
    public String key;

    public UploadSong(String title, String duration, String link) {

        if(title.trim().equals("")){
            title = "No title";
        }
        this.title = title;
        this.duration = duration;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
