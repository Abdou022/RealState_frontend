package com.poly.realstate.models;

public class OfferRequest {
    private int houseId;
    private int applicantId;

    public OfferRequest(int houseId, int applicantId) {
        this.houseId = houseId;
        this.applicantId = applicantId;
    }

    // getters et setters si besoin
}