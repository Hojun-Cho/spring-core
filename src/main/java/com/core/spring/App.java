package com.core.spring;

import com.core.spring.domain.AppConfig;
import com.core.spring.domain.member.MemberRepository;
import com.core.spring.domain.member.MemberService;
import com.core.spring.domain.order.OrderService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class App {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        //컨테이너
        System.out.println(context.getApplicationName());
        Arrays.stream(context.getBeanDefinitionNames()).forEach(name -> System.out.println(name));
        Arrays.stream(context.getBeanDefinitionNames()).
                forEach(s -> System.out.println(s + " > " + context.isSingleton(s)));
        MemberService memberService= context.getBean("memberService", MemberService.class);
        OrderService orderService= context.getBean("orderService", OrderService.class);
        MemberRepository memberRepository =  context.getBean("memberRepository", MemberRepository.class);
    }
}