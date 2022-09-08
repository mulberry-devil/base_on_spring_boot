package com.caston.base_on_spring_boot.designpatterns.prototype;

public class ProductManager {
    private Product product = new Product("huawei", "0", "");

    public Product createPro(String color) {
        Product o = (Product) product.clone();
        o.setColor(color);
        return o;
    }
}
