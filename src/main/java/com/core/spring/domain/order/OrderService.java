package com.core.spring.domain.order;

public interface OrderService {
    Order createOrder(Long memberId,String itemName,int itemPrice);
}
