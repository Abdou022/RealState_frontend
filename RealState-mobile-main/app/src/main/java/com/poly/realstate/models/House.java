package com.poly.realstate.models;

import com.google.gson.annotations.SerializedName;

public class House {
    private int id;
    private String title;
    private String description;
    private String price;
    private String address;
    private String surface;
    private String rooms;
    private String image;
    @SerializedName("ownerName")
    private String ownerName;

    // Getters et setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getAddress() { return address; }
    public String getSurface() { return surface; }
    public String getRooms() { return rooms; }
    public String getImage() { return image; }
    public String getOwnerName() { return ownerName; }
}
