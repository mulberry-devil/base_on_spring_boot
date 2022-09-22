package com.caston.base_on_spring_boot.designpatterns.bridging;

public class FingerPrintMode implements PayMode{
    @Override
    public void paymode() {
        System.out.println("通过指纹支付");
    }
}
