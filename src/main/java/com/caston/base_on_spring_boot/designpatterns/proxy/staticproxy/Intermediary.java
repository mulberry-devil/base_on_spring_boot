package com.caston.base_on_spring_boot.designpatterns.proxy.staticproxy;

public class Intermediary implements Rent {

    private Rent rent;

    public Intermediary(Rent rent) {
        this.rent = rent;
    }

    @Override
    public void rent() {
        System.out.println("进行其他操作");
        rent.rent();
        System.out.println("进行其他操作...");
    }
}
