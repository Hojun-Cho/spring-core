package com.core.spring.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentContext implements Context {
    private final Map<String, Object> cglibObject;
    private final Map<String, Class> originalClass;

    public ComponentContext(Map<String, Object> cglibObject, Map<String, Class> originalClass) {
        this.cglibObject = cglibObject;
        this.originalClass = originalClass;
    }




    // TODO 오브젝트중에 Autowired 선언된 필드가 존재하면 호출시 컨테이너에서 읽어온다
    @Override
    public Object getBean(String methodName) {
        return null;
    }
}
