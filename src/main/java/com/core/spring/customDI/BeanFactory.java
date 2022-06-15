//package com.core.spring.customDI;
//
//import net.sf.cglib.proxy.Enhancer;
//import net.sf.cglib.proxy.MethodInterceptor;
//import net.sf.cglib.proxy.MethodProxy;
//
//import java.lang.reflect.Method;
//import java.util.List;
//
//import static com.core.spring.customDI.AllClassesLoader.find;
//
//public class BeanFactory {
//    private final InstanceContainer container;
//    private final List<Class<?>> classes;
//
//    public BeanFactory() {
//        classes = find("com.core");
//        this.container = new InstanceContainer(Core.makeInstance(classes));
//    }
//
//    public Object init() {
//        classes.stream().forEach(aClass -> {
//            Enhancer enhancer = new Enhancer();
//            enhancer.setSuperclass(aClass.getClass());
//            enhancer.setCallback(new MethodInterceptor() {
//                @Override
//                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
//                    if (!container.isExist(method.getName())) {
//                        System.out.println("not exits >> " + method.getName());
//                        container.add(method.getName(), proxy.invokeSuper(obj, args));
//                    }
//                    return container.getInstance(method.getName());
//                }
//            });
//
//
//        });
//
//
//    }
//}
