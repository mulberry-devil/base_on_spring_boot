package com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.ali;

import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.ali.product.AliProduct1;
import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.ali.product.AliProduct2;
import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.AbstractFactory;
import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.product.Product1;
import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.product.Product2;

public class AliFactory implements AbstractFactory {
    @Override
    public Product1 createProduct1() {
        return new AliProduct1();
    }

    @Override
    public Product2 createProduct2() {
        return new AliProduct2();
    }
}
