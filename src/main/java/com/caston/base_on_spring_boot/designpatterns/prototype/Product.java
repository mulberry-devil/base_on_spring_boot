package com.caston.base_on_spring_boot.designpatterns.prototype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Product implements Cloneable {

    private String name;
    private String age;
    private String color;

    @Override
    protected Object clone() {
        Product product = null;
        try {
            if (product == null) {
                product = (Product) super.clone();
                // 可以进行其他调整性的操作，比如给属性排序乱序等操作
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }
}
