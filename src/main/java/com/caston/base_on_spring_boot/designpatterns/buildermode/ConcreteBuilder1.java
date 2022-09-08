package com.caston.base_on_spring_boot.designpatterns.buildermode;

public class ConcreteBuilder1 implements Builder {

    @Override
    public Builder buildPart1() {
        product.setPart1("builder mysql part1");
        return this;
    }

    @Override
    public Builder buildPart2() {
        product.setPart2("builder mysql part2");
        return this;
    }

    @Override
    public Builder buildPart3() {
        product.setPart3("builder mysql part3");
        return this;
    }

    @Override
    public Product1 getProduct() {
        // 添加自己的生成规则判断
        return product;
    }
}
