package com.caston.base_on_spring_boot.designpatterns.factory.factorymethod.factory;

import com.caston.base_on_spring_boot.designpatterns.factory.simplefactory.factory.product.ConcreteProduct2;
import com.caston.base_on_spring_boot.designpatterns.factory.simplefactory.factory.product.Product;

public class ConcreteFactory2 implements AbstractFactory {
    @Override
    public Product createProduct() {
        return new ConcreteProduct2();
    }
}
