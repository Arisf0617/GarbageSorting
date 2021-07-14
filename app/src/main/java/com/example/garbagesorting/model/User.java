package com.example.garbagesorting.model;

import java.io.Serializable;

public class User implements Serializable {
    private String phone;
    private String password;
    private Integer pic;

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public User(String phone, String password, Integer pic) {
        this.phone = phone;
        this.password = password;
        this.pic = pic;
    }

    public Integer getPic() {
        return pic;
    }

    public void setPic(Integer pic) {
        this.pic = pic;
    }

    public User() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", pic=" + pic +
                '}';
    }
}
