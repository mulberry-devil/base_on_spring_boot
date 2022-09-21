package com.caston.base_on_spring_boot.designpatterns.proxy.jdkroxy;

public class Client {
    public static void main(String[] args) {
        Landlord landlord = new Landlord();
        ProxyFactory proxyFactory = new ProxyFactory(landlord);
        Rent proxy = (Rent) proxyFactory.getProxyInstance();
        proxy.rent();
    }
}
