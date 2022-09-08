package com.caston.base_on_spring_boot.designpatterns.buildermode;

public class ConcreteBuilder2 implements Builder {
    @Override
    public void build() {
        product.setPart1("builder sql part1").setPart2("builder sql part2").setPart3("builder sql part3");
    }

    @Override
    public Product1 getProduct() {
        // 添加自己的生成规则判断
        return product;
    }
}
