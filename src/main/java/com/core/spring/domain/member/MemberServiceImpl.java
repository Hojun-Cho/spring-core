package com.core.spring.domain.member;

public class MemberServiceImpl implements  MemberService{
    private final MemberRepository memberRepository;

    public MemberServiceImpl() {
        this.memberRepository = new MemoryMemberRepository();
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
