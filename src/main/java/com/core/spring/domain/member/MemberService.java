package com.core.spring.domain.member;

public interface MemberService {

    void join(Member member);

    Member findMember(Long memberId);

    MemberRepository getMemberRepository();
}
