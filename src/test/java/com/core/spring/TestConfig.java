package com.core.spring;

@AnnotationTest
public class TestConfig {

    @CustomBean
    public String hello(){
        return "hello";
    }
    @CustomBean
    public String world(){
        return "world";
    }
}
