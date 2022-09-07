package com.caston.base_on_spring_boot.designpatterns.factory.factorymethod.factory;

import com.caston.base_on_spring_boot.designpatterns.factory.simplefactory.factory.product.Product;

public interface AbstractFactory {
    public Product createProduct();
}
