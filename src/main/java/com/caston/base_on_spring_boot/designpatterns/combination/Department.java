package com.caston.base_on_spring_boot.designpatterns.combination;

public class Department extends Component {

    public Department(String name, String des) {
        super(name, des);
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
        System.out.println("名称：" + getName() + "描述：" + getMsg());
    }
}