package com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory;

import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.ali.AliFactory;
import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.factory.AbstractFactory;
import com.caston.base_on_spring_boot.designpatterns.factory.abstractfactory.tencent.TencentFactory;

/**
 * 抽象工厂模式
 * 是一种为访问类提供一个创建一组相关或相互依赖对象的接口，且访问类无须指定索要产品的具体类就能获得同族的不同等级的产品的模式结构。
 */
public class Client {
    public static void main(String[] args) {
        AbstractFactory aliFactory = new AliFactory();
        AbstractFactory tencentFactory = new TencentFactory();

        aliFactory.createProduct1().show();
        aliFactory.createProduct2().show();

        tencentFactory.createProduct1().show();
        tencentFactory.createProduct2().show();
    }
}
