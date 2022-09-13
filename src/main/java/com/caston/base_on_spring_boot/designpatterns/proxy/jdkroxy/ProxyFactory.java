package com.caston.base_on_spring_boot.designpatterns.proxy.jdkroxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory implements InvocationHandler {

    private Object target;

    public ProxyFactory(Object target) {
        this.target = target;
    }

    public Object getProxyInstance() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        System.out.println("JDK动态代理开始...");
        Object invoke = method.invoke(target, objects);
        System.out.println("JDK动态代理结束...");
        return invoke;
    }
}
