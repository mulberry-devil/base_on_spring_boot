package com.caston.base_on_spring_boot.designpatterns.proxy.cglibproxy;

public class Client {
    public static void main(String[] args) {
        ProxyFactory proxyFactory = new ProxyFactory(Landlord.class);
        Landlord proxy1 = (Landlord) proxyFactory.getProxyInstance();
        proxy1.rent();

        Landlord landlord = new Landlord();
        ProxyFactory factory = new ProxyFactory(landlord);
        Landlord proxy2 = (Landlord) factory.getProxyInstance();
        proxy2.rent();
    }
}
