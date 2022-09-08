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

    public void setPart1(Object part1) {
        this.part1 = part1;
    }

    public void setPart2(Object part2) {
        this.part2 = part2;
    }

    public void setPart3(Object part3) {
        this.part3 = part3;
    }
}
