package com.caston.base_on_spring_boot.designpatterns.builder;

public interface Builder {
    Product1 product = new Product1();

    Builder buildPart1();
    Builder buildPart2();
    Builder buildPart3();

    Product1 getProduct();
}
