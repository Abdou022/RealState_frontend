package com.poly.realstate.models;

public class LoginResponse {

    private String message;
    private UserData user;

    public String getMessage() { return message; }
    public UserData getUser() { return user; }

    public static class UserData {
        private int id;
        private String fullName;
        private String email;
        private String phone;
        private String image;

        public int getId() { return id; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getImage() { return image; }
    }
}
