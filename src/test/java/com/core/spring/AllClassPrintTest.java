package com.core.spring;

import com.core.spring.customDI.InstanceContainer;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.core.spring.customDI.AllClassesLoader.find;
import static com.core.spring.customDI.Core.makeInstance;
import static org.junit.jupiter.api.Assertions.*;

public class AllClassPrintTest {
    private InstanceContainer container;
    public static Map<String, Object> containers = new HashMap<>();

//    @BeforeEach
//    void init() {
//        container = new InstanceContainer(makeInstance(find("com.core")));
//    }

    @Test
    void runMyContainer() {
        InstanceContainer container = new InstanceContainer(makeInstance(find("com.core")));

        assertThrows(NoSuchBeanDefinitionException.class,
                () -> container.getInstance("NOTEXISTMETHOD"));

        assertTrue(container.getInstance("hello") instanceof String);
    }

    @Test
    void classExtends() {
        Map<String, Object> instances = new HashMap<>();

        find("com.core").parallelStream().forEach(aClass -> {
            try {
                Object object = aClass.getDeclaredConstructor().newInstance();
                ClassLoader classLoader = aClass.getClassLoader();
                if (aClass.getDeclaredAnnotationsByType(MyConfiguration.class).length != 0)
                    Arrays.stream(aClass.getDeclaredMethods()).
                            filter(method -> method.getParameterCount() == 0 &&
                                    method.getDeclaredAnnotationsByType(CustomBean.class) != null)
                            .forEach(method -> {
                                System.out.println("make ====" + object.getClass() + " >> " + method.getName() + "==== make");
                                try {
                                    instances.put(method.getName(), method.invoke(object));
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                }
                            });
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            }
        });

    }

    @Test
    void cglib() {
        List<Class<?>> classes = find("com.core");
        Map<String, Object> cglibClasses = new HashMap<>();
        for (Class<?> c : classes) {
            if (c.getDeclaredAnnotationsByType(MyConfiguration.class).length != 0)
                System.out.println("hah");
        }
        List<Class<?>> classList = classes.stream()
                .filter(aClass -> aClass.getDeclaredAnnotationsByType(MyConfiguration.class).length != 0)
                .collect(Collectors.toList());

        classList.stream()
                .forEach(nowClass -> {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(nowClass);
                    enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> proxy.invokeSuper(obj, args));
                    cglibClasses.put(nowClass.getSimpleName(), enhancer.create());
                });

        for (String key : cglibClasses.keySet()){
            Arrays.stream(cglibClasses.get(key).getClass().getDeclaredMethods())
                    .forEach(method -> System.out.println(method.getName()));
        }
        cglibClasses.keySet()
                .forEach(key ->
                        Arrays.stream(cglibClasses.get(key).getClass().getDeclaredMethods())
                                .filter(method -> method.getDeclaredAnnotationsByType(CustomBean.class).length != 0)
                                .forEach(method -> {
                                    System.out.println(method.getName());
                                    try {
                                        containers.put(method.getName(), method.invoke(cglibClasses.get(key)));
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }));


    }


}


