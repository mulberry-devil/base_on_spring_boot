package com.caston.base_on_spring_boot.designpatterns.bridging;

public class WXPay extends PayType {
    public WXPay(PayMode payMode) {
        this.payMode = payMode;
    }
}
