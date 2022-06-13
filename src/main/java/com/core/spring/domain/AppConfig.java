package com.core.spring.domain;

import com.core.spring.domain.member.MemberRepository;
import com.core.spring.domain.member.MemberService;
import com.core.spring.domain.member.MemberServiceImpl;
import com.core.spring.domain.member.MemoryMemberRepository;
import com.core.spring.domain.order.FixedDiscountPolicy;
import com.core.spring.domain.order.OrderService;
import com.core.spring.domain.order.OrderServiceImpl;
import com.core.spring.domain.order.RateDiscountPolicy;

public class AppConfig {
    public MemberRepository memberRepository(){
        return new MemoryMemberRepository();
    }

    public MemberService memberService() {
        return new MemberServiceImpl(new MemoryMemberRepository());
    }

    public OrderService orderService() {
        return new OrderServiceImpl(new MemoryMemberRepository()
                , new RateDiscountPolicy());
    }
    public OrderService FixOrderService(){
        return new OrderServiceImpl(new MemoryMemberRepository()
                , new FixedDiscountPolicy());
    }
}
