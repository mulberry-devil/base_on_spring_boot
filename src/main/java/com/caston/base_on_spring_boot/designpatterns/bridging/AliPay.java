package com.caston.base_on_spring_boot.designpatterns.bridging;

public class AliPay extends PayType {
    public AliPay(PayMode payMode) {
        this.payMode = payMode;
    }
}
