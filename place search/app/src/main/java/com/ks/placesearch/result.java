package com.ks.placesearch;

import java.io.Serializable;

public class result implements Serializable{

    private String name;
    private String address;
    private String icon;
    private String placeId;
    private String position;

    public result(String name, String address, String icon, String placeId, String position) {
        this.name = name;
        this.address = address;
        this.icon = icon;
        this.placeId = placeId;
        this.position = position;
    }


    public String getIcon() { return icon; }

    public void setIcon(String icon) { this.icon = icon; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getPlaceId() { return placeId; }

    public void setPlaceId(String placeId) { this.placeId = placeId; }

    public String getPosition() { return position; }

    public void setPosition(String position) { this.position = position; }
}
