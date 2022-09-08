package com.caston.base_on_spring_boot.designpatterns.prototype;

/**
 * 原型模式
 * 创建重复对象，适合用于创建属性大部分相同的对象
 */
public class Client {
    public static void main(String[] args) {
        // 批量生产
        ProductManager productManager = new ProductManager();
        for (int i = 0; i < 10; i++) {
            System.out.println(productManager.createPro("red").toString());
        }
        System.out.println(productManager.createPro("black").toString());
    }
}
