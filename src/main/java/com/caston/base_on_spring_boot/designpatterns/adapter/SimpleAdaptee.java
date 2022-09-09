package com.caston.base_on_spring_boot.designpatterns.adapter;

public class SimpleAdaptee implements Adaptee {
    @Override
    public void adapterRequest() {
        System.out.println("输出适配后的结果...");
    }
}
