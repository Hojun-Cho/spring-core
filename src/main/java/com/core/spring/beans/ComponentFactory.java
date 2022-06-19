package com.core.spring.beans;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.core.spring.classLoader.Loader.find;

public class ComponentFactory implements Factory {
    public static final String PATTERN = "**";
    private final List<String> packageNames = new ArrayList<>();
    private final Map<String, Class> packageToComponentClasses = new ConcurrentHashMap<>();
    private final Map<String, Object> containers = new ConcurrentHashMap<>();
    private final Map<String, Object> cglibClass = new ConcurrentHashMap<>();
    private final Map<String, Constructor> autowiredConstructors = new ConcurrentHashMap<>();
    private final Map<String, Class> originalClass = new HashMap<>();

    public ComponentFactory() {
        Map<String, Enhancer> tempEnhancer = new ConcurrentHashMap<>();
        find("com.core")
                .stream()
                .filter(aClass -> aClass.getDeclaredAnnotation(MyComponentScan.class) != null &&
                        packageNames.stream().allMatch(name ->
                                !name.contains(aClass.getDeclaredAnnotation(MyComponentScan.class).value())))
                .forEach(aClass -> {
                    String packageName = aClass.getDeclaredAnnotation(MyComponentScan.class).value();
                    packageNames.add(packageName);
                    find(packageName)
                            .stream()
                            .filter(nowClass -> nowClass.getDeclaredAnnotation(MyComponent.class) != null)
                            .forEach(nowClass -> {
                                findAutowiredConstructor(nowClass);
                                packageToComponentClasses.put(aClass.getSimpleName().concat(PATTERN + nowClass.getSimpleName()), nowClass);
                                originalClass.put(nowClass.getSimpleName(), nowClass);

                                Enhancer enhancer = new Enhancer();
                                enhancer.setSuperclass(nowClass);
                                enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                                    if (!containers.containsKey(method.getName()))
                                        containers.put(method.getName(), proxy.invokeSuper(obj, args));
                                    return containers.get(method.getName());
                                });
                                cglibClass.put(nowClass.getSimpleName(), enhancer.create());

                                tempEnhancer.put(nowClass.getSimpleName(), enhancer);
                            });
                });
        autowiredConstructors.keySet().stream()
                .forEach(key -> {
                    Enhancer enhancer = tempEnhancer.get(key);
                    Constructor constructor = autowiredConstructors.get(key);
                    List<Object> classes = Arrays.stream(constructor.getParameterTypes())
                            .map(type -> {
                                return cglibClass.get(type.getSimpleName());
                            })
                            .collect(Collectors.toList());
                    cglibClass.put(key, enhancer.create(constructor.getParameterTypes(), classes.toArray()));
                });


    }

    @Override
    public Context getContext(Class<?> targetClass) {
        Map<String, Object> cglibObject = new HashMap<>();
        Map<String, Class> originalClass = new HashMap<>();
        packageToComponentClasses.keySet()
                .parallelStream()
                .filter(key -> key.startsWith(targetClass.getSimpleName() + PATTERN))
                .map(key -> key.split("\\*\\*")[1])
                .forEach(key -> {
                    cglibObject.put(key, cglibClass.get(key));
                    originalClass.put(key, packageToComponentClasses.get(targetClass.getSimpleName() + PATTERN + key));
                });
        return new ComponentContext(cglibObject, originalClass);
    }

    public void findAutowiredConstructor(Class nowClass) {
        Optional<Constructor> result = Arrays.stream(nowClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getDeclaredAnnotation(MyAutowired.class) != null)
                .findFirst();
        if (result.isPresent()) {
            autowiredConstructors.put(result.get().getDeclaringClass().getSimpleName(), result.get());

        }
    }

}
