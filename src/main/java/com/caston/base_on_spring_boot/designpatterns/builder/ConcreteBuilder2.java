package com.caston.base_on_spring_boot.designpatterns.builder;

public class ConcreteBuilder2 implements Builder {

    @Override
    public Builder buildPart1() {
        product.setPart1("builder sql part1");
        return this;
    }

    @Override
    public Builder buildPart2() {
        product.setPart2("builder sql part2");
        return this;
    }

    @Override
    public Builder buildPart3() {
        product.setPart3("builder sql part3");
        return this;
    }

    @Override
    public Product1 getProduct() {
        // 添加自己的生成规则判断
        return product;
    }
}
