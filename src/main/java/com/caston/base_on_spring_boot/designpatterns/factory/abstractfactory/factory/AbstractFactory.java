package com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory;

import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.product.Product1;
import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.product.Product2;

public interface AbstractFactory {
    public Product1 createProduct1();

    public Product2 createProduct2();
}
