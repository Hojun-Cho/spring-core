package com.core.spring;

import com.core.spring.customDI.InstanceContainer;
import com.core.spring.domain.member.MemberRepository;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

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
    void cglib() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Class<?>> classes = find("com.core");
        Map<String, Object> cglibClasses = new HashMap<>();
        List<Class<?>> classList = classes.stream()
                .filter(aClass -> aClass.getDeclaredAnnotationsByType(MyConfiguration.class).length != 0)
                .collect(Collectors.toList());

        classList.stream()
                .forEach(nowClass -> {
                    System.out.println(nowClass.getName());
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(nowClass);
                    enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> proxy.invokeSuper(obj, args));
                    cglibClasses.put(nowClass.getSimpleName(), enhancer.create());
                });

        for (String key : cglibClasses.keySet()) {
            System.out.println("====================");
            Object target = cglibClasses.get(key);
            target = (TestConfig) target;
            System.out.println(target.getClass());
            Method memberRepository = target.getClass().getMethod("memberRepository");
            MemberRepository repository = (MemberRepository) memberRepository.invoke(target);
        }

//        cglibClasses.keySet()
//                .forEach(key ->
//                        Arrays.stream(cglibClasses.get(key).getClass().getDeclaredMethods())
//                                .filter(method -> method.getDeclaredAnnotationsByType(CustomBean.class).length != 0)
//                                .forEach(method -> {
//                                    System.out.println(method.getName());
//                                    try {
//                                        containers.put(method.getName(), method.invoke(cglibClasses.get(key)));
//                                    } catch (IllegalAccessException | InvocationTargetException e) {
//                                        e.printStackTrace();
//                                    }
//                                }));


    }


}


