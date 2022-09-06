package com.caston.base_on_spring_boot.designpatterns.singleton;

/**
 * 饿汉模式写法之一
 * 不存在线程安全问题
 */
public enum SingletonEnum {
    INSTANCE;

    public void otherMethod() {

    }
}
