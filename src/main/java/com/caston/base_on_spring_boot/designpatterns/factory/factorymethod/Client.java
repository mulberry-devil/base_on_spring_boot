package com.caston.base_on_spring_boot.designpatterns.factory.factorymethod;

import com.caston.base_on_spring_boot.designpatterns.factory.factorymethod.factory.product.ConcreteProduct1;
import com.caston.base_on_spring_boot.designpatterns.factory.factorymethod.factory.product.ConcreteProduct2;
import com.caston.base_on_spring_boot.designpatterns.factory.factorymethod.factory.product.Product;

/**
 * 工厂方法模式
 * 可以使系统在不修改原来代码的情况下引进新的产品
 * 适合生产同一产品等级的产品，比如电视总类，汽车总类，无法细分产品族，当要宝马汽车和宝马其他相关产品时，需要使用抽象工厂模式
 */
public class Client {
    public static void main(String[] args) {
        Product product1 = new ConcreteProduct1();
        Product product2 = new ConcreteProduct2();
        product1.show();
        product2.show();
    }
}
