package com.ks.placesearch;

import java.io.Serializable;

public class Review implements Serializable{

    private String photo;
    private String name;
    private String rating;
    private String time;
    private String comment;
    private String url;

    public Review(String name, String photo, String rating, String time, String comment, String url) {
        this.photo = photo;
        this.name = name;
        this.rating = rating;
        this.time = time;
        this.comment = comment;
        this.url = url;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

}
