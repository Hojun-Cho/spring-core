package com.core.spring;

import com.core.spring.customDI.InstanceContainer;
import com.core.spring.domain.member.MemberRepository;
import net.sf.cglib.proxy.*;
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
        Map<String, Object> containers = new HashMap<>();
        classList.stream()
                .forEach(nowClass -> {
                    System.out.println(nowClass.getSimpleName());
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(nowClass);
                    enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                        System.out.println("============im in ====================");
                        if (!containers.containsKey(method.getName())) {
                            System.out.println("&&&&&&&&&&&&&&&&& in container " + containers);
                            containers.put(method.getName(), proxy.invokeSuper(obj, args));
                        }
                        System.out.println("============im out ====================");
                        return containers.get(method.getName());
                    });
                    cglibClasses.put(nowClass.getSimpleName(), enhancer.create());
                });
        Method target = Arrays.stream(cglibClasses.get("TestConfig").getClass().getDeclaredMethods())
                .filter(method -> method.getName().contains("CGLIB") && method.getName().contains("memberRepository"))
                .findFirst()
                .get();
        System.out.println(target.getName());
        MemberRepository memberRepository1 = (MemberRepository) target.invoke(cglibClasses.get("TestConfig"));
        MemberRepository memberRepository2 = (MemberRepository) target.invoke(cglibClasses.get("TestConfig"));
        System.out.println(memberRepository1 + " --- " + memberRepository2);
    }


}


