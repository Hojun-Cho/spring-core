package com.core.spring;

import com.core.spring.customDI.AllClassesLoader;
import com.core.spring.customDI.Core;
import com.core.spring.customDI.InstanceContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.IntStream;

import static com.core.spring.customDI.AllClassesLoader.find;
import static org.junit.jupiter.api.Assertions.*;

public class AllClassPrintTest {
    private InstanceContainer container;

    @BeforeEach
    void init() {
        container = new InstanceContainer(Core.makeInstance(find("com.core")));
    }

    @Test
    void runMyContainer() {
        InstanceContainer container = new InstanceContainer(Core.makeInstance(find("com.core")));

        assertThrows(NoSuchBeanDefinitionException.class,
                () -> container.getInstance("NOTEXISTMETHOD"));

        assertTrue(container.getInstance("hello") instanceof String);
    }

    @Test
    void classExtends(){
        Map<String, Object> instances = new HashMap<>();

        find("com.core").stream().forEach(aClass -> {
            try {
                Object object = aClass.getDeclaredConstructor().newInstance() ;
                if (aClass.getDeclaredAnnotationsByType(AnnotationTest.class).length != 0)
                    Arrays.stream(aClass.getDeclaredMethods()).filter(method -> method.getParameterCount() == 0 &&
                                    method.getDeclaredAnnotationsByType(CustomBean.class) != null)
                            .forEach(method -> {

                                System.out.println("make ====" + object.getClass() + " >> "+ method.getName() + "==== make");
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
}

