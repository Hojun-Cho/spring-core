package com.core.spring.domain.member;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryMemberRepositoryTest {

    @Test
    void save() {
        MemberRepository store = new MemoryMemberRepository();
        Member member = new Member(1l,"hojun",Grade.BASIC);
        store.save(member);
        assertEquals(store.findById(1l),member);
    }


}