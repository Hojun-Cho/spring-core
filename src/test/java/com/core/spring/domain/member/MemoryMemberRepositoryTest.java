package com.core.spring.domain.member;

import com.core.spring.domain.AppConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryMemberRepositoryTest {

    @Test
    void save() {
        MemberRepository store = new AppConfig().memberRepository();
        Member member = new Member(1l,"hojun",Grade.BASIC);
        store.save(member);
        assertEquals(store.findById(1l),member);
    }


}