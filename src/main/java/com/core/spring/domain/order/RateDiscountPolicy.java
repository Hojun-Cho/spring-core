package com.core.spring.domain.order;

import com.core.spring.domain.member.Grade;
import com.core.spring.domain.member.Member;

public class RateDiscountPolicy implements DiscountPolicy{
    private final double discountRate= 0.1;
    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP){
            return (int) (price * discountRate);
        }
        return 0;
    }
}
