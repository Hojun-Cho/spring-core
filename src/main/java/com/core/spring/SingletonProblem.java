package com.core.spring;

@AnnotationTest
public class SingletonProblem {
    private  int  price;

    public SingletonProblem() {
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
