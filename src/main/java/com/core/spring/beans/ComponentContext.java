package com.core.spring.beans;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentContext implements Context {
    private final Map<String, Object> cglibObject;
    private final Map<String, Class> originalClass;
    private final Map<String, Constructor> autowiredConstructors = new ConcurrentHashMap<>();

    public ComponentContext(Map<String, Object> cglibObject, Map<String, Class> originalClass) {
        this.cglibObject = cglibObject;
        this.originalClass = originalClass;
    }

    public void findAutowiredConstructor() {
        this.originalClass.keySet().stream()
                .parallel()
                .forEach(key ->
                        Arrays.stream(originalClass.get(key).getDeclaredConstructors())
                                .filter(constructor -> constructor.getDeclaredAnnotation(MyAutowired.class) != null)
                                .forEach(constructor -> autowiredConstructors.put(constructor.getName(), constructor))
                );
    }

    // TODO 오브젝트중에 Autowired 선언된 필드가 존재하면 호출시 컨테이너에서 읽어온다
    @Override
    public Object getBean(String methodName) {
        return null;
    }
}
