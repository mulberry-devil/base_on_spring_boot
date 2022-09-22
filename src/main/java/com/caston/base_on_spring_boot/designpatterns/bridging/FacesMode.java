package com.caston.base_on_spring_boot.designpatterns.bridging;

public class FacesMode implements PayMode{
    @Override
    public void paymode() {
        System.out.println("通过人脸支付");
    }
}
