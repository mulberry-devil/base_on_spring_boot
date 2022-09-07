package com.caston.base_on_spring_boot.designpatterns.factory.simplefactory.factory.product;

public class ConcreteProduct2 implements Product {
    @Override
    public void show() {
        System.out.println("产品2展示...");
    }
}
