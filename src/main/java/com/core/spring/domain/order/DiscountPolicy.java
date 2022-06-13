package com.core.spring.domain.order;

import com.core.spring.domain.member.Member;

public interface DiscountPolicy {
    int discount(Member member, int Price);
}
