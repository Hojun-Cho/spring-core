package com.core.spring;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.core.spring.customDI.AllClassesLoader.find;

public class AllClassPrintTest {

    @Test
    void 어노테이션에_해당하는_함수들을_모두_호출한다() {
        List<Class<?>> classes = find("com.core");
        List<Object> result = new ArrayList<>();

        classes.stream().forEach(aClass -> {
            try {
                Object object = aClass.getDeclaredConstructor().newInstance();
                if (aClass.getDeclaredAnnotationsByType(AnnotationTest.class).length != 0)
                    Arrays.stream(aClass.getDeclaredMethods()).filter(method -> method.getParameterCount() == 0 &&
                                    method.getDeclaredAnnotationsByType(CustomBean.class) != null)
                            .forEach(method -> {
                                System.out.println("====" + object + "====");
                                try {
                                    result.add(method.invoke(object));
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                }
                            });
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            }
        });
    }

}
