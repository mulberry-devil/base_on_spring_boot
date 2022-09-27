package com.caston.base_on_spring_boot.designpatterns.tactics;

public class OperationSubtract implements Strategy {
    @Override
    public int doOperation(int num1, int num2) {
        return num1 - num2;
    }
}
