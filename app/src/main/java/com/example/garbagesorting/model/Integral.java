package com.example.garbagesorting.model;

//linmob
public class Integral {
    String phone;
    String in_out;
    String integral;
    String recovery;
    String date;
    String place;

    public Integral(String phone, String in_out, String integral, String date, String place) {
        this.phone = phone;
        this.in_out = in_out;
        this.integral = integral;
        this.date = date;
        this.place = place;
    }

    public Integral(String phone, String in_out, String integral, String recovery, String date, String place) {
        this.phone = phone;
        this.in_out = in_out;
        this.integral = integral;
        this.recovery = recovery;
        this.date = date;
        this.place = place;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIn_out() {
        return in_out;
    }

    public void setIn_out(String in_out) {
        this.in_out = in_out;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getRecovery() {
        return recovery;
    }

    public void setRecovery(String recovery) {
        this.recovery = recovery;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}