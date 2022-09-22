package com.caston.base_on_spring_boot.designpatterns.facade;

public class Facade {
    private SubClassA subClassA;
    private SubClassB subClassB;
    private SubClassC subClassC;

    public Facade() {
        // 可以通过单例模式生成并注入
        this.subClassA = new SubClassA();
        this.subClassB = new SubClassB();
        this.subClassC = new SubClassC();
    }

    public void invokeMethod() {
        subClassA.SubOperationA();
        subClassB.SubOperationB();
        subClassC.SubOperationC();
    }
}
