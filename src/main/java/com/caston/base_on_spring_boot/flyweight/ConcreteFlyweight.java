package com.caston.base_on_spring_boot.flyweight;

public class ConcreteFlyweight implements IFlyweight {
    private String intrinsicState;

    public ConcreteFlyweight(String intrinsicState) {
        this.intrinsicState = intrinsicState;
    }

    @Override
    public void operation(String extrinsicState) {
        System.out.println("这是外部状态操作方法：" + extrinsicState);
    }
}
