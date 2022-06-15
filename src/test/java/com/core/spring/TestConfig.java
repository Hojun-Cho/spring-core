package com.core.spring;

@MyConfiguration
public class TestConfig {

    @CustomBean
    public String hello(){
        System.out.println("=====================================================");
        return "hello";
    }
    @CustomBean
    public String world(){
        return "world";
    }
}
