package com.core.spring.domain.order;

import java.util.Objects;

public class Order {
    private Long memberId;
    private String itemName;
    private int itemPrice;
    private int duscountPrice;

    public Order(Long memberId, String itemName, int itemPrice, int duscountPrice) {
        this.memberId = memberId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.duscountPrice = duscountPrice;
    }
    public int calculatePrice(){
        return itemPrice - duscountPrice;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public int getDiscountPrice() {
        return duscountPrice;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setDuscountPrice(int duscountPrice) {
        this.duscountPrice = duscountPrice;
    }

    @Override
    public String toString() {
        return "Order{" +
                "memberId=" + memberId +
                ", itemName='" + itemName + '\'' +
                ", itemPrice=" + itemPrice +
                ", duscountPrice=" + duscountPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return itemPrice == order.itemPrice && duscountPrice == order.duscountPrice && Objects.equals(memberId, order.memberId) && Objects.equals(itemName, order.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, itemName, itemPrice, duscountPrice);
    }
}
