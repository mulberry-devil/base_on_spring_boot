package com.caston.base_on_spring_boot.designpatterns.proxy.staticproxy;

public class Client {
    public static void main(String[] args) {
        Landlord landlord = new Landlord();
        Intermediary intermediary = new Intermediary(landlord);
        intermediary.rent();
    }
}
