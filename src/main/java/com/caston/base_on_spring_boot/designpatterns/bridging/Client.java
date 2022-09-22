package com.caston.base_on_spring_boot.designpatterns.bridging;

public class Client {
    public static void main(String[] args) {
        PayType wxPay = new WXPay(new FingerPrintMode());
        PayType aliPay = new AliPay(new FacesMode());
    }
}
