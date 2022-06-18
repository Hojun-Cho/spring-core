package com.core.spring;

import com.core.spring.beans.MyComponent;
import com.core.spring.beans.MyConfiguration;

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
