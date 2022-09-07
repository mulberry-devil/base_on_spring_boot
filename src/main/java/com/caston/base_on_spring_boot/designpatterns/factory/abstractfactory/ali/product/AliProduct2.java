package com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.ali.product;

import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.product.Product2;

public class AliProduct2 implements Product2 {
    @Override
    public void show() {
        System.out.println("阿里产品2展示...");
    }
}
