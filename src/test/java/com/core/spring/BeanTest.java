package com.core.spring;

import com.core.spring.domain.AppConfig;
import com.core.spring.domain.member.Grade;
import com.core.spring.domain.member.Member;
import com.core.spring.domain.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class BeanTest {
//    private ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);


    @Test
    void 개체_클래스_읽어오기() throws ClassNotFoundException {
        Class<?> object = Class.forName("com.core.spring.domain.AppConfig");
        System.out.println("getName >> " + object.getName());
        System.out.println("getSimpleName >> " + object.getSimpleName());
        System.out.println("getPackageName >> " + object.getPackageName());
        System.out.println("getSuperclass >> " + object.getSuperclass().getName());
        System.out.println("getTypeName >> " + object.getTypeName());
        Arrays.stream(object.getDeclaredMethods()).forEach(method ->
                System.out.println("mehotd >> " + method));
    }

    @Test
    void 받은_메서드_정보출력() throws ClassNotFoundException, NoSuchMethodException {
        Class<?> object = Class.forName("com.core.spring.domain.AppConfig");
        Method method = object.getMethod("memberRepository");

        System.out.println(method.getDeclaringClass());
        System.out.println(method.getName());
        System.out.println(method.getParameterTypes());
        System.out.println(method.getReturnType());
    }

    @Test
    void 받은_메서드_실행() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> object = Class.forName("com.core.spring.domain.AppConfig");
        Method method = object.getMethod("memberRepository");
        Object appConfig = object.newInstance();

        MemberRepository repository = (MemberRepository) method.invoke(appConfig);
        assertDoesNotThrow(() -> repository.save(new Member(1L, "hojun", Grade.BASIC)));
    }

    @Test
    void 모든_클래스_순회() {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
         systemClassLoader.resources("").forEach(url -> System.out.println(url.toString()));
         Arrays.stream(systemClassLoader.getDefinedPackages())
                 .map(aPackage -> {
             Annotation[] declaredAnnotations = aPackage.getAnnotations();
             return declaredAnnotations;
         }).forEach(annotations ->
                 (Arrays.stream(annotations)).forEach(annotation -> System.out.println(annotation.annotationType())) );
    }
    @Test
    void 컨텍스트에서_AppConfig_꺼내오기() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println(context.getBean(AppConfig.class));
    }
}
