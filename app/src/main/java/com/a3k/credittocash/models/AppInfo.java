package com.a3k.credittocash.models;

public class AppInfo {

    private String paymentKeyProduction;
    private String paymentKeySandbox;
    private float appVersion;
    private float serviceChargePer;
    private int sla;
    private String slaUnit;

    public AppInfo() {
    }

    public String getPaymentKeyProduction() {
        return paymentKeyProduction;
    }

    public void setPaymentKeyProduction(String paymentKeyProduction) {
        this.paymentKeyProduction = paymentKeyProduction;
    }

    public String getPaymentKeySandbox() {
        return paymentKeySandbox;
    }

    public void setPaymentKeySandbox(String paymentKeySandbox) {
        this.paymentKeySandbox = paymentKeySandbox;
    }

    public float getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(float appVersion) {
        this.appVersion = appVersion;
    }

    public float getServiceChargePer() {
        return serviceChargePer;
    }

    public void setServiceChargePer(float serviceChargePer) {
        this.serviceChargePer = serviceChargePer;
    }

    public int getSla() {
        return sla;
    }

    public void setSla(int sla) {
        this.sla = sla;
    }

    public String getSlaUnit() {
        return slaUnit;
    }

    public void setSlaUnit(String slaUnit) {
        this.slaUnit = slaUnit;
    }
}
