package com.caston.base_on_spring_boot.designpatterns.decorator.decorator;

import com.caston.base_on_spring_boot.designpatterns.decorator.Beverage;

public class Boba implements ToppingDecorator {

    private Beverage beverage;

    public Boba(Beverage beverage) {
        this.beverage = beverage;
    }

    @Override
    public double cost() {
        System.out.println("add boba cost 1...");
        return 1 + beverage.cost();
    }
}
