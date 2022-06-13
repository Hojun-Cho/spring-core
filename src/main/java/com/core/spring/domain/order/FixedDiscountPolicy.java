package com.core.spring.domain.order;

import com.core.spring.domain.member.Grade;
import com.core.spring.domain.member.Member;

public class FixedDiscountPolicy implements DiscountPolicy {
    private final int discountFixAmount = 1000;

    @Override
    public int discount(Member member, int Price) {
        if (member.getGrade() == Grade.VIP){
            return discountFixAmount;
        }else {
            return 0;
        }
    }
}
