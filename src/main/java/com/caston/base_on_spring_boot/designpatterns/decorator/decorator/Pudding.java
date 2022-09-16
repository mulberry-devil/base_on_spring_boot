package com.caston.base_on_spring_boot.designpatterns.decorator.decorator;

import com.caston.base_on_spring_boot.designpatterns.decorator.Beverage;

public class Pudding implements ToppingDecorator {
    private Beverage beverage;

    public Pudding(Beverage beverage) {
        this.beverage = beverage;
    }

    @Override
    public double cost() {
        System.out.println("add Pudding cost 2...");
        return 2 + beverage.cost();
    }
}
