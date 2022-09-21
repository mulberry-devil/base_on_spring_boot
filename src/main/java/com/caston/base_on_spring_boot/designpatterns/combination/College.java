package com.caston.base_on_spring_boot.designpatterns.combination;

import java.util.ArrayList;
import java.util.List;

public class College extends Component {

    //List存放的是专业的信息
    List<Component> components = new ArrayList<>();

    public College(String name, String des) {
        super(name, des);
    }

    //实际业务中，University和College重写的add方法和remove方法可能不相同
    @Override
    protected Component add(Component component) {
        this.components.add(component);
        return this;
    }

    @Override
    protected Component remove(Component component) {
        this.components.remove(component);
        return this;
    }


    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getMsg() {
        return super.getMsg();
    }

    //打印College包含的专业的名字
    @Override
    protected void print() {
        System.out.println("===============名称：" + getName() + "描述：" + getMsg() + "===============");
        for (Component coms : components) {
            coms.print();
        }
    }
}