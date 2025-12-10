package com.poly.realstate.models;

public class OfferSent {
    private int id;
    private String status;
    private int houseId;
    private String houseTitle;
    private int creatorId;
    private String creatorName;
    private String createdAt;

    // Getters
    public int getId() { return id; }
    public String getStatus() { return status; }
    public int getHouseId() { return houseId; }
    public String getHouseTitle() { return houseTitle; }
    public int getCreatorId() { return creatorId; }
    public String getCreatorName() { return creatorName; }
    public String getCreatedAt() { return createdAt; }
}