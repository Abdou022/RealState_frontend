package com.poly.realstate.models;

import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private int id;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("image")
    private String image;

    // Getters
    public String getMessage() { return message; }
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getImage() { return image; }
}