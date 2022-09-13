package com.caston.base_on_spring_boot.designpatterns.proxy.cglibproxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ProxyFactory implements MethodInterceptor {
    private Class targetClass;
    private Object target;

    public ProxyFactory(Object target) {
        this.target = target;
    }

    public ProxyFactory(Class targetClass) {
        this.targetClass = targetClass;
    }

    public Object getProxyInstance() {
        //CGLIB的增强类对象
        Enhancer enhancer = new Enhancer();
        if (target != null) {
            //设置增强对象
            enhancer.setSuperclass(target.getClass());
        } else if (targetClass != null) {
            //设置增强对象
            enhancer.setSuperclass(targetClass);
        }
        //定义代理逻辑对象为当前对象，要求对象实现MethodInterceptor方法
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("CGLIB动态代理...");
        Object invoke = null;
        if (target != null) {
            System.out.println("通过传入实例创建");
            System.out.println(target.getClass().getName());
            System.out.println(o.getClass().getName());
            invoke = method.invoke(target, objects);
        } else if (targetClass != null) {
            System.out.println("直接通过类Class创建");
            invoke = methodProxy.invokeSuper(o, objects);
        }
        System.out.println("CGLIB动态代理...");
        return invoke;
    }
}
