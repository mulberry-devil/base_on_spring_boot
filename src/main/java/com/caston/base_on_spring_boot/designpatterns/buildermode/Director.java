package com.caston.base_on_spring_boot.designpatterns.buildermode;

public class Director {
    private Builder builder;

    public Director(Builder builder) {
        this.builder = builder;
    }

    public Product1 construct() {
        builder.build();
        Product1 product = builder.getProduct();
        return product;
    }
}
