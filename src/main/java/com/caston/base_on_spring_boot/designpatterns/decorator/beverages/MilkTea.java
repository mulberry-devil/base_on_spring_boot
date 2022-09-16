package com.caston.base_on_spring_boot.designpatterns.decorator.beverages;

import com.caston.base_on_spring_boot.designpatterns.decorator.Beverage;

public class MilkTea implements Beverage {
    @Override
    public double cost() {
        System.out.println("Milk Tea cost 5...");
        return 5;
    }
}
