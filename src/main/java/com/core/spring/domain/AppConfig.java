package com.core.spring.domain;

import com.core.spring.AnnotationTest;
import com.core.spring.CustomBean;
import com.core.spring.SingletonProblem;
import com.core.spring.domain.member.MemberRepository;
import com.core.spring.domain.member.MemberService;
import com.core.spring.domain.member.MemberServiceImpl;
import com.core.spring.domain.member.MemoryMemberRepository;
import com.core.spring.domain.order.OrderService;
import com.core.spring.domain.order.OrderServiceImpl;
import com.core.spring.domain.order.RateDiscountPolicy;
import org.springframework.context.annotation.Bean;

@AnnotationTest
public class AppConfig {

    @Bean
    @CustomBean
    public MemberRepository memberRepository(){
        return new MemoryMemberRepository();
    }
    @Bean
    @CustomBean
    public MemberService memberService() {
        return new MemberServiceImpl(null);
    }

    @Bean
    @CustomBean
    public OrderService orderService(){
        return new OrderServiceImpl(null
                , new RateDiscountPolicy());
    }
    @Bean
    @CustomBean
    public SingletonProblem singletonProblem(){
        return new SingletonProblem();
    }
}
