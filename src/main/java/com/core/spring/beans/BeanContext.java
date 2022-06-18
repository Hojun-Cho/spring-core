package com.core.spring.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class BeanContext implements Context {

    private final Map<String, Method> methodMap;
    private final Object clazz;

    public BeanContext(Object clazz, Map<String, Method> methodMap) {
        this.clazz = clazz;
        this.methodMap = methodMap;
    }

    public Object getBean(String methodName) {
        if (methodMap.containsKey(methodName)) {
            try {
                return methodMap.get(methodName).invoke(clazz);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
