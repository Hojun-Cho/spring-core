package com.core.spring.customDI;

import com.core.spring.MyConfiguration;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Core {
    private final  Map<String, Object> cglibClasses = new ConcurrentHashMap<>();
    private final Map<String, Object> enhancers = new ConcurrentHashMap<>();
    private final Map<String, Class> original = new HashMap<>();
    private final Map<String, Method> methods = new ConcurrentHashMap<>();

    public Core(List<Class<?>> classes) {
        classes.stream().parallel()
                .filter(aClass -> aClass.getDeclaredAnnotationsByType(MyConfiguration.class).length != 0)
                .forEach(nowClass -> {
                    original.put(nowClass.getSimpleName(), nowClass);
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(nowClass);
                    enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                        if (!enhancers.containsKey(method.getName()))
                            enhancers.put(method.getName(), proxy.invokeSuper(obj, args));
                        return enhancers.get(method.getName());
                    });
                    cglibClasses.put(nowClass.getSimpleName(), enhancer.create());
                });
        init();
    }

    public void init() {
        original.keySet()
                .forEach(key -> Arrays.stream(original.get(key).getDeclaredMethods())
                        .forEach(method -> methods.put(method.getName(), getEnhancerMethod(key, method.getName()))
                        ));
    }


    private Method getEnhancerMethod(String key, String targetMethod) {
         Method result= Arrays.stream(cglibClasses.get(key).getClass().getDeclaredMethods())
                .parallel()
                .filter(method -> method.getName().contains(targetMethod) && method.getName().length() == targetMethod.length())
                .findFirst().get();
         result.setAccessible(true);
         return result;
    }

    public Object getCustomBean(Class declareClass ,String methodName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return methods.get(methodName).invoke(cglibClasses.get(declareClass.getSimpleName()));
    }

}

