package com.poly.realstate;

public class House {

    private int imageRes;
    private String title;
    private String price;
    private String address;
    private String area;
    private String rooms;
    private String description;

    public House(int imageRes, String title, String price, String address, String area, String rooms, String description) {
        this.imageRes = imageRes;
        this.title = title;
        this.price = price;
        this.address = address;
        this.area = area;
        this.rooms = rooms;
        this.description = description;
    }

    public int getImageRes() { return imageRes; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getAddress() { return address; }
    public String getArea() { return area; }
    public String getRooms() { return rooms; }
    public String getDescription() { return description; }
}
