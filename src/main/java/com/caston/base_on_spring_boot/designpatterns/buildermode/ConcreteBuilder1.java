package com.caston.base_on_spring_boot.designpatterns.buildermode;

public class ConcreteBuilder1 implements Builder {

    @Override
    public void build() {
        product.setPart1("builder mysql part1").setPart2("builder mysql part2").setPart3("builder mysql part3");
    }

    @Override
    public Product1 getProduct() {
        // 添加自己的生成规则判断
        return product;
    }
}
