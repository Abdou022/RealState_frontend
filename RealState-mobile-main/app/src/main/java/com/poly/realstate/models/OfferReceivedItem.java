package com.poly.realstate.models;

public class OfferReceivedItem {
    private int id;
    private String status;
    private int houseId;
    private String houseTitle;
    private int applicantId;
    private String applicantName;
    private String createdAt;

    // Getters
    public int getId() { return id; }
    public String getStatus() { return status; }
    public void setStatus(String status) {  this.status=status; }
    public int getHouseId() { return houseId; }
    public String getHouseTitle() { return houseTitle; }
    public int getApplicantId() { return applicantId; }
    public String getApplicantName() { return applicantName; }
    public String getCreatedAt() { return createdAt; }
}
