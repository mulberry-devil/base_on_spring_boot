package com.caston.base_on_spring_boot.designpatterns.buildermode;

public interface Builder {
    Product1 product = new Product1();

    void build();

    Product1 getProduct();
}
