package com.caston.base_on_spring_boot.designpatterns.combination;

import java.util.ArrayList;
import java.util.List;

public class University extends Component {
    List<Component> components = new ArrayList<>();

    public University(String name, String msg) {
        super(name, msg);
    }

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

    @Override
    protected void print() {
        System.out.println("===============名称：" + getName() + "描述：" + getMsg() + "===============");
        for (Component component : components) {
            component.print();
        }
    }
}
