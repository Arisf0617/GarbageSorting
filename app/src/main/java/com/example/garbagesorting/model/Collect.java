package com.example.garbagesorting.model;

import java.io.Serializable;

public class Collect implements Serializable {
    String phone;
    String collectionphone;
    String finddate;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCollectionphone() {
        return collectionphone;
    }

    public void setCollectionphone(String collectionphone) {
        this.collectionphone = collectionphone;
    }

    public String getFinddate() {
        return finddate;
    }

    public void setFinddate(String finddate) {
        this.finddate = finddate;
    }

    public Collect(String phone, String collectionphone, String finddate) {
        this.phone = phone;
        this.collectionphone = collectionphone;
        this.finddate = finddate;
    }

    public Collect() {
    }
}
