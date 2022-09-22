package com.caston.base_on_spring_boot.designpatterns.bridging;

public class PasswordMode implements PayMode{
    @Override
    public void paymode() {
        System.out.println("通过密码支付");
    }
}
