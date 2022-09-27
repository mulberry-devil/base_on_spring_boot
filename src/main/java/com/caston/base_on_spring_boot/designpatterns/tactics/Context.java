package com.caston.base_on_spring_boot.designpatterns.tactics;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class Context {
    private Map<String, BiFunction<Integer, Integer, Integer>> strategyMap = new HashMap<>(3);

    // 模拟初始化
    public void init() {
        strategyMap.put("add", (arg1, arg2) -> new OperationAdd().doOperation(arg1, arg2));
        strategyMap.put("sub", (arg1, arg2) -> new OperationSubtract().doOperation(arg1, arg2));
    }

    public Integer operatorByStrategy(Integer arg1, Integer arg2, String key) {
        BiFunction<Integer, Integer, Integer> biFunction = strategyMap.get(key);
        if (Objects.isNull(biFunction)) {
            return new OperationAdd().doOperation(arg1, arg2);
        }
        return biFunction.apply(arg1, arg2);
    }
}
