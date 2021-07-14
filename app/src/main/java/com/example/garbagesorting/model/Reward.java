package com.example.garbagesorting.model;

//linmob
public class Reward {
    String reward;
    String date;
    String integral;

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public Reward(String reward, String date, String integral) {
        this.reward = reward;
        this.date = date;
        this.integral = integral;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}