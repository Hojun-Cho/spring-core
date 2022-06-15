package com.core.spring;

import com.core.spring.customDI.Core;
import com.core.spring.customDI.InstanceContainer;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.core.spring.customDI.AllClassesLoader.find;
import static com.core.spring.customDI.Core.makeInstance;
import static org.junit.jupiter.api.Assertions.*;

public class AllClassPrintTest {
    private InstanceContainer container;

    @BeforeEach
    void init() {
        container = new InstanceContainer(makeInstance(find("com.core")));
    }

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
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(TestConfig.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                if(!container.isExist(method.getName())) {
                    System.out.println("not exits >> " + method.getName() );
                    container.add(method.getName() ,proxy.invokeSuper(obj,args));
                }
                return container.getInstance(method.getName());
            }
        });
        TestConfig testConfig = (TestConfig) enhancer.create();
        System.out.println(testConfig.hello());
        System.out.println(testConfig.world());

        System.out.println(new TestConfig().hello());
    }
}


