package com.caston.base_on_spring_boot.designpatterns.proxy.staticproxy;

public class Landlord implements Rent{
    @Override
    public void rent() {
        System.out.println("静态代理出租房子...");
    }
}
