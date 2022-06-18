package com.core.spring.beans;


public interface Factory {
    CustomContext getContext(Class<?> targetClass);
}
