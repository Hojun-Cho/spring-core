package com.core.spring.beans;

import com.core.spring.MyConfiguration;

import com.core.spring.TestConfig;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Factory {
    private final Map<String, Object> cglibClass = new ConcurrentHashMap<>();
    private final Map<String, Object> containers = new ConcurrentHashMap<>();
    private final Map<String, Class> original = new HashMap<>();
    private final Map<String, Method> methods = new ConcurrentHashMap<>();

    public Factory(List<Class<?>> classes) {
        classes.stream().parallel()
                .filter(aClass -> aClass.getDeclaredAnnotationsByType(MyConfiguration.class).length != 0)
                .forEach(nowClass -> {
                    original.put(nowClass.getSimpleName(), nowClass);
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(nowClass);
                    enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                        if (!containers.containsKey(method.getName()))
                            containers.put(method.getName(), proxy.invokeSuper(obj, args));
                        return containers.get(method.getName());
                    });
                    cglibClass.put(nowClass.getSimpleName(), enhancer.create());
                });
        init();
    }

    public CustomContext getContext(Class<TestConfig> targetClass) {
        Map<String,Method> contextMethodMap = new HashMap<>();
        Arrays.stream(original.get(targetClass.getSimpleName())
                        .getDeclaredMethods())
                .forEach(method ->
                        contextMethodMap.put(method.getName(),methods.get(method.getName())));

        return new CustomContext(cglibClass.get(targetClass.getSimpleName()),Collections.unmodifiableMap(contextMethodMap));
    }


    public void init() {
        original.keySet()
                .forEach(key -> Arrays.stream(original.get(key).getDeclaredMethods())
                        .forEach(method -> methods.put(method.getName(), getCglibMethod(key, method.getName()))
                        ));
    }


    private Method getCglibMethod(String key, String targetMethod) {
        Method result = Arrays.stream(cglibClass.get(key).getClass().getDeclaredMethods())
                .parallel()
                .filter(method -> method.getName().equals(targetMethod))
                .findFirst().get();
        result.setAccessible(true);
        return result;
    }

    public Object getCustomBean(Class declareClass, String methodName) throws InvocationTargetException, IllegalAccessException {
        return methods.get(methodName).invoke(cglibClass.get(declareClass.getSimpleName()));
    }

}
