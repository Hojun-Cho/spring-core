package com.core.spring.customDI;

import com.core.spring.MyConfiguration;
import com.core.spring.domain.member.Member;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Core {
    private final Map<String, Object> cglibClasses = new ConcurrentHashMap<>();
    private final Map<String, Object> containers = new ConcurrentHashMap<>();
    private final Map<String,Object> original = new HashMap<>();
    public Core(List<Class<?>> classes) {
        classes.stream().parallel()
                .filter(aClass -> aClass.getDeclaredAnnotationsByType(MyConfiguration.class).length != 0)
                .forEach(nowClass -> {
                    original.put(nowClass.getSimpleName(),nowClass);
                    System.out.println(nowClass.getSimpleName() + "  confirm");
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(nowClass);
                    enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                        if (!containers.containsKey(method.getName()))
                            containers.put(method.getName(), proxy.invokeSuper(obj, args));
                        return containers.get(method.getName());
                    });
                    cglibClasses.put(nowClass.getSimpleName(), enhancer.create());
                });
        init();
    }

    public void init() {
        cglibClasses.keySet().stream()
                .forEach(key -> {
                    Object object = cglibClasses.get(key);
                    Arrays.stream(object.getClass().getDeclaredMethods())
                            .forEach(method -> {
                                System.out.println(method.getDeclaringClass()+"  " + object.getClass().getSimpleName());
                                if (method.getDeclaringClass().getName().equals(object.getClass().getName())) {
                                    try {
                                        containers.put(method.getName(), method.invoke(original.get(object.getClass().getSimpleName())));
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                });
    }

    public Object getCustomBean(Class aclass, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (containers.containsKey(aclass.getSimpleName())) {
            Class<?> object = containers.get(aclass.getSimpleName()).getClass();
            Method method = object.getDeclaredMethod(methodName);
            return method.invoke(object);
        }
        throw new NoSuchMethodException(aclass.getSimpleName() + " no such class");
    }
}
