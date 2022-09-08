package com.caston.base_on_spring_boot.designpatterns.builder;

public class Director {
    private Builder builder;

    public Director(Builder builder) {
        this.builder = builder;
    }

    public Product1 construct() {
        builder.buildPart1().buildPart2().buildPart3();
        Product1 product = builder.getProduct();
        return product;
    }
}
