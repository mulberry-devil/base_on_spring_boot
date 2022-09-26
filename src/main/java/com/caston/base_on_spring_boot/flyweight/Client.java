package com.caston.base_on_spring_boot.flyweight;

public class Client {
    public static void main(String[] args) {
        IFlyweight flyweight1 = FlyweightFactory.getFlyweight("实例1");
        IFlyweight flyweight2 = FlyweightFactory.getFlyweight("实例2");

        flyweight1.operation("操作1");
        flyweight2.operation("操作2");
    }
}
