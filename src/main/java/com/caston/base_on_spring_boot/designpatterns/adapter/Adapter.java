package com.caston.base_on_spring_boot.designpatterns.adapter;

public class Adapter implements Target {
    Adaptee adaptee;

    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void request() {
        adaptee.adapterRequest();
    }
}
