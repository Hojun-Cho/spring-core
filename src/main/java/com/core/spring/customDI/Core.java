package com.core.spring.customDI;

import com.core.spring.AnnotationTest;
import com.core.spring.CustomBean;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.core.spring.customDI.AllClassesLoader.find;

public class Core {
    public static Map<String, Object> makeInstance() {
        List<Class<?>> classes = find("com.core");
        Map<String, Object> instances = new HashMap<>();

        classes.stream().forEach(aClass -> {
            try {
                Object object = aClass.getDeclaredConstructor().newInstance();
                if (aClass.getDeclaredAnnotationsByType(AnnotationTest.class).length != 0)
                    Arrays.stream(aClass.getDeclaredMethods()).filter(method -> method.getParameterCount() == 0 &&
                                    method.getDeclaredAnnotationsByType(CustomBean.class) != null)
                            .forEach(method -> {
                                System.out.println("====" + object + "====");
                                try {
                                    instances.put(method.getName(), method.invoke(object));
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                }
                            });
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            }
        });

        return Collections.unmodifiableMap(instances);
    }
}
