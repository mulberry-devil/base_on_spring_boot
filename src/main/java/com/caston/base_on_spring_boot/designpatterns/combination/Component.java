package com.caston.base_on_spring_boot.designpatterns.combination;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public abstract class Component {
    private String name;
    private String msg;

    protected Component add(Component component) {
        throw new UnsupportedOperationException();
    }

    protected Component remove(Component component) {
        throw new UnsupportedOperationException();
    }

    protected abstract void print();
}
