package com.core.spring.domain.member;

import com.core.spring.beans.MyAutowired;
import com.core.spring.beans.MyComponent;

public class MemberServiceImpl implements  MemberService{
    private final MemberRepository memberRepository;

    @MyAutowired
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberRepository getMemberRepository() {
        return memberRepository;
    }


}
