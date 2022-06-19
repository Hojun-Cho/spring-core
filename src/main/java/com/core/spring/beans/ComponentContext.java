package com.core.spring.beans;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class ComponentContext {
    private final Map<Class, Object> cglibObject;
    private final Map<String, Class> originalClass;


    public ComponentContext(Map<Class, Object> cglibObject, Map<String, Class> originalClass) {
        this.cglibObject = cglibObject;
        this.originalClass = originalClass;
    }


    // TODO 오브젝트중에 Autowired 선언된 필드가 존재하면 호출시 컨테이너에서 읽어온다
    public Object getBean(Class wantClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
         Class result=  cglibObject.keySet().stream().
                filter(key ->
                        Arrays.stream(key.getInterfaces())
                                .filter(aClass -> aClass.getSimpleName().equals(wantClass.getSimpleName()))
                                .findFirst().stream().findFirst().isPresent())
                .findFirst()
                .get();
         return  cglibObject.get(result);

    }
}
