package com.caston.base_on_spring_boot.designpatterns.decorator;

import com.caston.base_on_spring_boot.designpatterns.decorator.beverages.MilkTea;
import com.caston.base_on_spring_boot.designpatterns.decorator.decorator.Boba;
import com.caston.base_on_spring_boot.designpatterns.decorator.decorator.Pudding;

public class Client {
    public static void main(String[] args) {
        Beverage tea = new Boba(new Pudding(new MilkTea()));
        System.out.println(tea.cost());
    }
}
