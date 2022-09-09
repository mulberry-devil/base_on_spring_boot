package com.caston.base_on_spring_boot.designpatterns.adapter;

public class Client {
    public static void main(String[] args) {
        Adaptee adaptee = new SimpleAdaptee();
        new Adapter(adaptee).request();
    }
}
