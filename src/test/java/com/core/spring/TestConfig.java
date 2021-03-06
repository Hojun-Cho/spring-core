package com.core.spring;

import com.core.spring.beans.CustomBean;
import com.core.spring.beans.MyComponent;
import com.core.spring.beans.MyComponentScan;
import com.core.spring.beans.MyConfiguration;
import com.core.spring.domain.member.MemberRepository;
import com.core.spring.domain.member.MemberService;
import com.core.spring.domain.member.MemberServiceImpl;
import com.core.spring.domain.member.MemoryMemberRepository;
import com.core.spring.domain.order.OrderService;
import com.core.spring.domain.order.OrderServiceImpl;
import com.core.spring.domain.order.RateDiscountPolicy;

@MyConfiguration
@MyComponentScan( value ="com.core")
public class TestConfig {

    @CustomBean
    public MemberRepository memberRepository(){
        return new MemoryMemberRepository();
    }
    @CustomBean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @CustomBean
    public OrderService orderService(){
        return new OrderServiceImpl(memberRepository()
                , new RateDiscountPolicy());
    }

}

