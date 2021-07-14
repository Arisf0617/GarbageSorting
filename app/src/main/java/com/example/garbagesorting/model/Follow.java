package com.example.garbagesorting.model;

//linmob
public class Follow {
    String phone;
    String follow;
    String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Follow(String phone, String follow, String icon) {
        this.phone = phone;
        this.follow = follow;
        this.icon = icon;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public Follow(String phone, String follow) {
        this.phone = phone;
        this.follow = follow;
    }
}