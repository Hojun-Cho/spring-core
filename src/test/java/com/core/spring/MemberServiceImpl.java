package com.core.spring;

import com.core.spring.beans.MyAutowired;
import com.core.spring.beans.MyComponent;

@MyComponent
public class MemberServiceImpl implements MemberService {
    private  MemberRepository memberRepository;

    public MemberServiceImpl() {
    }

    @MyAutowired
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberRepository getMemberRepository() {
        return memberRepository;
    }


}
