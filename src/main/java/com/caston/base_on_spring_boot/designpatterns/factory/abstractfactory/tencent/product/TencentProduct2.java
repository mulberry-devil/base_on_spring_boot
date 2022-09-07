package com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.tencent.product;

import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.product.Product2;

public class TencentProduct2 implements Product2 {
    @Override
    public void show() {
        System.out.println("腾讯产品2展示...");
    }
}
