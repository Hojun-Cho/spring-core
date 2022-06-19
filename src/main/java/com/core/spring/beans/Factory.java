package com.core.spring.beans;


public interface Factory {
    Context getContext(Class<?> targetClass);
}
