package com.core.spring.beans;


public interface Factory {
    BeanContext getContext(Class<?> targetClass);
}
