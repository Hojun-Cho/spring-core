package com.core.spring.domain.member;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientTest {
    MemberService memberService = new MemberServiceImpl();
    @Test
    void 멤버_저장() {
        Member member = new Member(1L, "hojun", Grade.VIP);

        memberService.join(member);
        Member findMember = memberService.findMember(member.getId());

        assertEquals(member,findMember);
    }

}
