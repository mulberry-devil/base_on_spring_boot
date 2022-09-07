package com.caston.base_on_spring_boot.designpatterns.factory.simplefactory;

import com.caston.base_on_spring_boot.designpatterns.factory.simplefactory.factory.SimpleFactory;
import com.caston.base_on_spring_boot.designpatterns.factory.simplefactory.factory.product.Product;

/**
 * 简单工厂模式
 * 传入描述参数生成对应产品
 * 工厂类单一，职责过重，一旦增加新产品不得不修改工厂逻辑
 */
public class Client {
    public static void main(String[] args) {
        Product product1 = SimpleFactory.createProduct(1);
        Product product2 = SimpleFactory.createProduct(2);
        product1.show();
        product2.show();
    }
}
