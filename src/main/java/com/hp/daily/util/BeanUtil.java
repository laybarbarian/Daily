package com.hp.daily.util;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
import java.lang.reflect.Field;
import java.util.Map;

public class BeanUtil {
    private BeanUtil() {
    }

    // only used for property type Class
    public static Object getBean(Map<String,String> reqMap,Class cls){
        Object target =null;
        try {
            target = cls.newInstance();
            for (Map.Entry<String, String> entry : reqMap.entrySet()) {
                System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
                Field f = cls.getDeclaredField(entry.getKey());
                f.setAccessible(true);
                f.set(target,entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return target;
    }
}
