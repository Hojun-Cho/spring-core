package com.core.spring;

import com.core.spring.domain.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println(context.getApplicationName());
        Arrays.stream(context.getBeanDefinitionNames()).forEach(name -> System.out.println(name));
        Arrays.stream(context.getBeanDefinitionNames()).
                forEach(s -> System.out.println(s+" > " +context.isSingleton(s)));
    }
}
