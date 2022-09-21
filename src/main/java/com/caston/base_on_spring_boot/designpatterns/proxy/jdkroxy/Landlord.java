package com.caston.base_on_spring_boot.designpatterns.proxy.jdkroxy;

public class Landlord implements Rent {
    @Override
    public void rent() {
        System.out.println("JDK动态代理出租房子...");
    }
}
