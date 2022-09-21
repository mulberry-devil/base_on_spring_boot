package com.caston.base_on_spring_boot.designpatterns.decorator.beverages;

import com.caston.base_on_spring_boot.designpatterns.decorator.Beverage;

public class FruitTea implements Beverage {
    @Override
    public double cost() {
        System.out.println("Fruit Tea cost 6.5...");
        return 6.5;
    }
}
