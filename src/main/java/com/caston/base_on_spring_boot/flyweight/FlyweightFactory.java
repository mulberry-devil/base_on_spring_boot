package com.caston.base_on_spring_boot.flyweight;

import java.util.HashMap;
import java.util.Map;

public class FlyweightFactory {
    private static Map<String, IFlyweight> pool = new HashMap<String, IFlyweight>();

    public static IFlyweight getFlyweight(String intrinsicState) {
        if (!pool.containsKey(intrinsicState)) {
            IFlyweight flyweight = new ConcreteFlyweight(intrinsicState);
            pool.put(intrinsicState, flyweight);
        }
        return pool.get(intrinsicState);
    }
}
