package com.caston.base_on_spring_boot.designpatterns.factory.simplefactory.factory;

import com.caston.base_on_spring_boot.designpatterns.factory.simplefactory.factory.product.ConcreteProduct1;
import com.caston.base_on_spring_boot.designpatterns.factory.simplefactory.factory.product.ConcreteProduct2;
import com.caston.base_on_spring_boot.designpatterns.factory.simplefactory.factory.product.Product;

public class SimpleFactory {
    public static Product createProduct(int index) {
        switch (index) {
            case 1:
                return new ConcreteProduct1();
            case 2:
                return new ConcreteProduct2();
        }
        return null;
    }
}
