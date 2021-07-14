package com.example.garbagesorting.model;

import java.io.Serializable;

public class find implements Serializable {
    private String phone;
    private String text;
    private String pic;
    private String recyclable_garbage;
    private String time;
    public find(String phone, String text, String pic, String recyclable_garbage,String time) {
        this.phone = phone;
        this.text = text;
        this.pic = pic;
        this.recyclable_garbage = recyclable_garbage;
        this.time=time;
    }
    public find(String phone, String text,String recyclable_garbage,String time) {
        this.phone = phone;
        this.text = text;
        this.recyclable_garbage = recyclable_garbage;
        this.time=time;
    }

    public find() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getRecyclable_garbage() {
        return recyclable_garbage;
    }

    public void setRecyclable_garbage(String recyclable_garbage) {
        this.recyclable_garbage = recyclable_garbage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "find{" +
                "phone='" + phone + '\'' +
                ", text='" + text + '\'' +
                ", pic='" + pic + '\'' +
                ", recyclable_garbage='" + recyclable_garbage + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
