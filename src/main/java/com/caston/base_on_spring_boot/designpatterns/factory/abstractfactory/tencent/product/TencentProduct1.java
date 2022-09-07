package com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.tencent.product;

import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.product.Product1;

public class TencentProduct1 implements Product1 {
    @Override
    public void show() {
        System.out.println("腾讯产品1展示...");
    }
}
