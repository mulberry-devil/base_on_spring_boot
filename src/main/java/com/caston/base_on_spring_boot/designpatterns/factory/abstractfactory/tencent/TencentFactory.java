package com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.tencent;

import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.AbstractFactory;
import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.product.Product1;
import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.product.Product2;
import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.tencent.product.TencentProduct1;
import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.tencent.product.TencentProduct2;

public class TencentFactory implements AbstractFactory {
    @Override
    public Product1 createProduct1() {
        return new TencentProduct1();
    }

    @Override
    public Product2 createProduct2() {
        return new TencentProduct2();
    }
}
