package com.core.spring.domain;

import com.core.spring.beans.MyConfiguration;
import com.core.spring.beans.CustomBean;
import com.core.spring.domain.member.MemberRepository;
import com.core.spring.domain.member.MemberService;
import com.core.spring.domain.member.MemberServiceImpl;
import com.core.spring.domain.member.MemoryMemberRepository;
import com.core.spring.domain.order.OrderService;
import com.core.spring.domain.order.OrderServiceImpl;
import com.core.spring.domain.order.RateDiscountPolicy;

//@Configuration
@MyConfiguration
public class AppConfig {

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
