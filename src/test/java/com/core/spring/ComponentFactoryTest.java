package com.core.spring;

import com.core.spring.beans.ComponentFactory;
import com.core.spring.beans.MyComponentScan;
import org.junit.jupiter.api.Test;
class ComponentFactoryTest {

    @Test
    void init(){
     ComponentFactory factory = new ComponentFactory();
     factory.getContext(TestConfig.class);
    }

}