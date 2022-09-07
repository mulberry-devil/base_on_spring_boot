package com.caston.base_on_spring_boot.designpatterns.factory.factorymethod.factory.product;

public class ConcreteProduct1 implements Product {
    @Override
    public void show() {
        System.out.println("产品1展示...");
    }
}
