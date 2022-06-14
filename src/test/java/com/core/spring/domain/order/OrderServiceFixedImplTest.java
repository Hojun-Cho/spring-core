//package com.core.spring.domain.order;
//
//import com.core.spring.domain.AppConfig;
//import com.core.spring.domain.member.Grade;
//import com.core.spring.domain.member.Member;
//import com.core.spring.domain.member.MemberRepository;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class OrderServiceFixedImplTest {
//    private MemberRepository memberRepository = new AppConfig().memberRepository();
//    private OrderService orderService = new AppConfig().orderService();
//
//    @Test
//    void 주문_고정() {
//        Member member = new Member(1L, "HOJUN", Grade.VIP);
//        memberRepository.save(member);
//
//        Order order = orderService.createOrder(member.getId(), member.getName(), 5000);
//
//        assertEquals(order, new Order(member.getId(), member.getName(), 5000, 1000));
//        assertEquals(order.calculatePrice(), 4000);
//    }
//}
