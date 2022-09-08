package com.caston.base_on_spring_boot.designpatterns.buildermode;

public class Product1 {
    private Object part1;
    private Object part2;
    private Object part3;

    public Object getPart1() {
        return part1;
    }

    public Object getPart2() {
        return part2;
    }

    public Object getPart3() {
        return part3;
    }

    public Product1 setPart1(Object part1) {
        this.part1 = part1;
        return this;
    }

    public Product1 setPart2(Object part2) {
        this.part2 = part2;
        return this;
    }

    public Product1 setPart3(Object part3) {
        this.part3 = part3;
        return this;
    }
}
