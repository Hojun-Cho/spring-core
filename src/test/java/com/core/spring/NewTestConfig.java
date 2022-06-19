package com.core.spring;

import com.core.spring.beans.CustomBean;
import com.core.spring.beans.MyComponent;
import com.core.spring.beans.MyConfiguration;

@MyConfiguration
public class NewTestConfig {

    @CustomBean
    void helloWorld(){
        System.out.println("hello world");
    }
}
