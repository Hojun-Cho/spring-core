package com.core.spring.customDI;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.Map;

public class InstanceContainer {
    private final Map<String, Object> instances ;
    public InstanceContainer(Map<String, Object> instances) {
        this.instances = instances;
    }

    public  Object getInstance(String methodName) {
        if(instances.containsKey(methodName))
            return instances.get(methodName);
        throw new NoSuchBeanDefinitionException(methodName +"는 존재하지 않는 bean입니다");
    }
    public boolean isExist(String methodName){
        return instances.containsKey(methodName);
    }

    public void add(String name, Object object) {
        this.instances.put(name,object);
    }
    public Map<String,Object> getInstances(){
        return instances;
    }
}
