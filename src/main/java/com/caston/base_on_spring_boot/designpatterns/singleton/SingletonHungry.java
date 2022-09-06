package com.caston.base_on_spring_boot.designpatterns.singleton;

/**
 * 饿汉模式
 * 项目启动时就初始化完成，所以不存在线程安全问题，缺点是如果项目没使用该实例会造成内存浪费
 */
public class SingletonHungry {
    private static SingletonHungry instance = new SingletonHungry();

    private SingletonHungry() {
    }

    public static SingletonHungry getInstance() {
        return instance;
    }
}
