package com.a3k.credittocash.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String name;
    private String picUrl;
    private String email;
    private String userId;
    private List<PaymentMethod> paymentMethods = new ArrayList<>();
    private double totalMoneyCashed;
    private String notificationToken;


    public  User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getTotalMoneyCashed() {
        return totalMoneyCashed;
    }

    public void setTotalMoneyCashed(double totalMoneyCashed) {
        this.totalMoneyCashed = totalMoneyCashed;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }
}
