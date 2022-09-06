package com.caston.base_on_spring_boot.designpatterns.singleton;

/**
 * 懒汉模式
 * 只有项目在使用的时候才会初始化
 */
public class SingletonLazy {

    private SingletonLazy() {
    }

    /*
     * 1.
     *  这种方式会造成线程不安全的情况
     */
    private static SingletonLazy instance1;

    public static SingletonLazy getInstance1() {
        if (instance1 == null) {
            instance1 = new SingletonLazy();
        }
        return instance1;
    }

    /*
     * 2.
     *  instance2 = new SingletonLazy();
     *  分为三步：1. 给对象分配内存空间 2. 初始化对象 3. 将地址赋给instance2
     *  所以可能会产生指令重排变成 1-> 3 -> 2
     *  当多线程进入判断时，有可能因为指令重排判断instance2不为空，但是实际还没初始化对象，导致有线程获取一个不完整的对象实例
     *  所以要加volatile保证可见性
     */
    private static volatile SingletonLazy instance2;

    public static SingletonLazy getInstance2() {
        if (instance2 == null) { // 优化多线程下每个去获取锁的资源消耗
            synchronized (SingletonLazy.class) {
                if (instance2 == null) { // 防止后续有线程是在等待锁的情况，当获取锁后再判断一次
                    instance2 = new SingletonLazy();
                }
            }
        }
        return instance2;
    }

    /*
     * 3.
     *  外部类初次加载，会初始化静态变量、静态代码块、静态方法，但不会加载内部类和静态内部类。
     *  内部类只有在使用的时候才会被加载
     *  不存在线程安全问题
     */
    private static class SingletonInner {
        private static final SingletonLazy instance3 = new SingletonLazy();
    }

    public static SingletonLazy getInstance3() {
        return SingletonInner.instance3;
    }
}
