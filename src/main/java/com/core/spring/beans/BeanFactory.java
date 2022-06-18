package com.core.spring.beans;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory implements Factory {
    private final Map<String, Object> cglibClass = new ConcurrentHashMap<>();
    private final Map<String, Object> containers = new ConcurrentHashMap<>();
    private final Map<String, Class> original = new HashMap<>();
    private final Map<String, Method> methods = new ConcurrentHashMap<>();
    public BeanFactory(List<Class<?>> classes) {
        classes.stream().parallel()
                .filter(aClass -> aClass.getDeclaredAnnotation(MyConfiguration.class) != null)
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

    public CustomContext getContext(Class<?> targetClass) {
        Map<String,Method> contextMethodMap = new HashMap<>();
        Arrays.stream(original.get(targetClass.getSimpleName())
                        .getDeclaredMethods())
                .forEach(method ->
                        contextMethodMap.put(method.getName(),methods.get(method.getName())));

        return new CustomContext(cglibClass.get(targetClass.getSimpleName()),Collections.unmodifiableMap(contextMethodMap));
    }

    private void init() {
        original.keySet()
                .forEach(key -> Arrays.stream(original.get(key).getDeclaredMethods())
                        .filter(method -> method.getDeclaredAnnotationsByType(CustomBean.class).length != 0)
                        .parallel()
                        .forEach(method ->
                                methods.put(method.getName(), getMethodDontCareOrder(key, method.getName()))
                        ));
    }

    private Method getMethodDontCareOrder(String key, String targetMethod) {
        Method result = Arrays.stream(cglibClass.get(key).getClass().getDeclaredMethods())
                .parallel()
                .filter(method -> method.getName().equals(targetMethod))
                .findFirst().get();
        result.setAccessible(true);
        return result;
    }
}

