package com.caston.base_on_spring_boot.designpatterns.facade;

public class Client {
    public static void main(String[] args) {
        Facade facade = new Facade();
        facade.invokeMethod();
    }
}
