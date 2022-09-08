package com.caston.base_on_spring_boot.designpatterns.buildermode;

/**
 * 建造者模式
 * 将对象的创建和表示过程进行分离，按开发者的规则进行对象实例的生成
 * 第一种方式使用静态内部类来构建对象的生成
 * 第二种方式结合工厂方法模式，包含 产品 抽象建造者（抽象工厂） 具体建造者（具体工厂） 指挥者
 */
public class Client {
    public static void main(String[] args) {
        // 1.
        Product build = new Product.Builder().setHost("127.0.0.1").setPort("6379").build();
        System.out.println(build.getHost() + build.getPort());
        // --------------------------------------------------------
        // 2.
        Product1 construct = new Director(new ConcreteBuilder1()).construct();
        System.out.println(construct.getPart1().toString() + construct.getPart2() + construct.getPart3());
        Product1 construct1 = new Director(new ConcreteBuilder2()).construct();
        System.out.println(construct1.getPart1().toString() + construct1.getPart2() + construct1.getPart3());
    }
}
