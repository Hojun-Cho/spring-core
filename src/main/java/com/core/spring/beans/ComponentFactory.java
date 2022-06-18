package com.core.spring.beans;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.core.spring.classLoader.Loader.find;

public class ComponentFactory implements Factory {
    public static final String PATTERN = "**";
    private  final List<String> packageNames = new ArrayList<>();
    private final Map<String, Object> packageToComponentClasses = new ConcurrentHashMap<>();
    private final Map<String, Object> containers = new ConcurrentHashMap<>();
    private final Map<String, Object> cglibClass = new ConcurrentHashMap<>();

    public ComponentFactory() {
        find("com.core")
                .stream()
                .filter(aClass -> aClass.getDeclaredAnnotation(MyComponentScan.class) != null &&
                        packageNames.stream().allMatch(name->
                                !name.contains(aClass.getDeclaredAnnotation(MyComponentScan.class).value())))
                .forEach(aClass -> {
                    String packageName = aClass.getDeclaredAnnotation(MyComponentScan.class).value();
                    packageNames.add(packageName);
                    find(packageName)
                            .stream()
                            .filter(nowClass -> nowClass.getDeclaredAnnotation(MyComponent.class) != null)
                            .forEach(nowClass -> {
                                packageToComponentClasses.put(aClass.getSimpleName().concat(PATTERN + nowClass.getSimpleName()), nowClass);

                                Enhancer enhancer = new Enhancer();
                                enhancer.setSuperclass(nowClass);
                                enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                                    if (!containers.containsKey(method.getName()))
                                        containers.put(method.getName(), proxy.invokeSuper(obj, args));
                                    return containers.get(method.getName());
                                });
                                cglibClass.put(nowClass.getSimpleName(), enhancer.create());
                            });
                });
    }


    @Override
    public CustomContext getContext(Class<?> targetClass) {
        Map<String, Object> cglibObject = new HashMap<>();
        Map<String, Object> originalClass = new HashMap<>();
        packageToComponentClasses.keySet().stream()
                .filter(key -> key.startsWith(targetClass.getSimpleName() + PATTERN))
                .map(key -> key.split("\\*\\*")[1])
                .forEach(key -> {
                    cglibObject.put(key, cglibClass.get(key));
                    originalClass.put(key, packageToComponentClasses.get(targetClass.getSimpleName() + PATTERN + key));
                });
        return null;
    }
}
