package com.core.spring.domain.order;

import com.core.spring.domain.member.MemberRepository;

public class OrderServiceImpl implements OrderService{
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy ;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    public MemberRepository getMemberRepository() {
        return memberRepository;
    }

    public DiscountPolicy getDiscountPolicy() {
        return discountPolicy;
    }


}
