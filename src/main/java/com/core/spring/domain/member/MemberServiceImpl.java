package com.core.spring.domain.member;

public class MemberServiceImpl implements  MemberService{
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberRepository getMemberRepository() {
        return memberRepository;
    }


}
