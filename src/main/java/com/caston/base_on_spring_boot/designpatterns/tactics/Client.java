package com.caston.base_on_spring_boot.designpatterns.tactics;

public class Client {
    public static void main(String[] args) {
        Context context = new Context();
        context.init();
        Integer add = context.operatorByStrategy(3, 5, "add");
        Integer sub = context.operatorByStrategy(10, 4, "sub");
        System.out.println(add + " " + sub);
    }
}
