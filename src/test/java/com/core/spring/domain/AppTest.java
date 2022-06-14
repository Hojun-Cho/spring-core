package com.core.spring.domain;

import com.core.spring.domain.member.MemberRepository;
import com.core.spring.domain.member.MemberService;
import com.core.spring.domain.member.MemoryMemberRepository;
import com.core.spring.domain.order.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    void 컨텍스트_확인() {
        //컨테이너
        System.out.println(context.getApplicationName());
        Arrays.stream(context.getBeanDefinitionNames()).forEach(name -> System.out.println(name));
        Arrays.stream(context.getBeanDefinitionNames()).
                forEach(s -> System.out.println(s + " > " + context.isSingleton(s)));
        MemberService memberService = context.getBean("memberService", MemberService.class);
        OrderService orderService = context.getBean("orderService", OrderService.class);
        MemberRepository memberRepository = context.getBean("memberRepository", MemberRepository.class);
    }

    @Test
    void 없는_빈() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean("NOTHING"));
    }

    @Test
    void 타입으로_조회() {
        assertTrue(context.getBean(MemberService.class) != null);
    }

    @Test
    void 동일_타입의_빈이_2개이상_존재하면_error() {
        context = new AnnotationConfigApplicationContext(AppConfig2.class);

        assertThrows(NoUniqueBeanDefinitionException.class,
                () -> context.getBean(MemberRepository.class));
    }

    @Test
    void 동일_타입의_빈이_2개이상_존재하면_이름_지정() {
        context = new AnnotationConfigApplicationContext(AppConfig2.class);

        assertDoesNotThrow(() -> context.getBean("memberRepository1", MemberRepository.class));
    }
    @Test
    void Bean의_상속관계(){
       Map<String,Object> o = context.getBeansOfType(Object.class);
       o.keySet().stream().forEach(
               key-> System.out.println("\n"+ key+ " >>> " + o.get(key).toString()+"\n"));
    }

    static class AppConfig2 {
        @Bean
        public MemberRepository memberRepository1() {
            return new MemoryMemberRepository();
        }

        @Bean
        public MemberRepository memberRepository2() {
            return new MemoryMemberRepository();
        }
    }

}
