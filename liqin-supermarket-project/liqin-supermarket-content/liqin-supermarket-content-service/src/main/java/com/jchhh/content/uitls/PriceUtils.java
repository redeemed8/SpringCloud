package com.jchhh.content.uitls;

import javax.validation.constraints.Max;
import java.util.HashMap;
import java.util.Map;

public class PriceUtils {

    public static final String MAX = "max";
    public static final String MIN = "min";

    public static Map<String, Integer> getMaxAndMinPrice(Integer price) {
        if (price == null) {
            return null;
        }
        Map<String, Integer> map = new HashMap<>();
        if (price <= 0) {
            map.put(MIN, -2);
            map.put(MAX, -1);
        } else if (price <= 25) {
            map.put(MIN, 1);
            map.put(MAX, 50);
        } else {
            map.put(MIN, price - price / 2);
            map.put(MAX, price + price / 2);
        }
        return map;
    }

}
