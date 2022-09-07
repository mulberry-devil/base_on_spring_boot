package com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.ali.product;

import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.product.Product1;

public class AliProduct1 implements Product1 {
    @Override
    public void show() {
        System.out.println("阿里产品1展示...");
    }
}
