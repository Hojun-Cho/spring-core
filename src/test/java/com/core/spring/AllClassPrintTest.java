package com.core.spring;

import com.core.spring.beans.*;
import com.core.spring.domain.member.MemberRepository;
import com.core.spring.domain.member.MemberService;
import com.core.spring.domain.member.MemberServiceImpl;
import com.core.spring.domain.order.OrderServiceImpl;
import net.sf.cglib.proxy.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static com.core.spring.classLoader.Loader.find;
import static org.junit.jupiter.api.Assertions.*;

public class AllClassPrintTest {
    Map<String, Object> cglibClasses = new HashMap<>();
    Map<String, Object> containers = new HashMap<>();
//    @BeforeEach
//    void init() {
//        container = new InstanceContainer(makeInstance(find("com.core")));
//    }


    @Test
    void classExtends() {
        Map<String, Object> instances = new HashMap<>();

        find("com.core").parallelStream().forEach(aClass -> {
            try {
                Object object = aClass.getDeclaredConstructor().newInstance();
                ClassLoader classLoader = aClass.getClassLoader();
                if (aClass.getDeclaredAnnotationsByType(MyConfiguration.class).length != 0)
                    Arrays.stream(aClass.getDeclaredMethods()).
                            filter(method -> method.getParameterCount() == 0 &&
                                    method.getDeclaredAnnotationsByType(CustomBean.class) != null)
                            .forEach(method -> {
                                System.out.println("make ====" + object.getClass() + " >> " + method.getName() + "==== make");
                                try {
                                    instances.put(method.getName(), method.invoke(object));
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                }
                            });
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            }
        });

    }

    @Test
    void cglib() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Class<?>> classes = find("com.core");
        List<Class<?>> classList = classes.stream()
                .filter(aClass -> aClass.getDeclaredAnnotationsByType(MyConfiguration.class).length != 0)
                .collect(Collectors.toList());
        classList.stream()
                .forEach(nowClass -> {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(nowClass);
                    enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                        System.out.println(method.getName() + "  " + obj.getClass() + "=====" + method.getDeclaringClass());
                        if (!containers.containsKey(method.getName()))
                            containers.put(method.getName(), proxy.invokeSuper(obj, args));
                        return containers.get(method.getName());
                    });
                    cglibClasses.put(nowClass.getSimpleName(), enhancer.create());
                });

        Object targetObject = cglibClasses.get("TestConfig");

        TestConfig myConfig = (TestConfig) targetObject;

        MemberRepository memberRepository1 = myConfig.memberRepository();
        MemberRepository memberRepository2 = myConfig.memberRepository();

        assertEquals(memberRepository1, memberRepository2);

        OrderServiceImpl orderService1 = (OrderServiceImpl) myConfig.orderService();
        OrderServiceImpl orderService2 = (OrderServiceImpl) myConfig.orderService();

        assertEquals(orderService1, orderService2);
        assertEquals(orderService1.getDiscountPolicy(), orderService2.getDiscountPolicy());
        assertEquals(orderService1.getMemberRepository(), orderService2.getMemberRepository());

    }

    @Test
    void CoreTest() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BeanFactory beanFactory = new BeanFactory(find("com.core"));
        BeanContext context = (BeanContext) beanFactory.getContext(TestConfig.class);
        assertTrue(context.getBean("memberRepository") != null);
        assertEquals(context.getBean("memberRepository"), context.getBean("memberRepository"));
        assertEquals(context.getBean("memberService"),
                context.getBean("memberService"));
        assertEquals(((MemberServiceImpl) context.getBean("memberService")).getMemberRepository(),
                ((MemberServiceImpl) context.getBean("memberService")).getMemberRepository());
    }

    @Test
    void getContext() {
        BeanContext context = (BeanContext) new BeanFactory(find("com.core")).getContext(TestConfig.class);

        assertTrue(context != null);
        assertTrue(context.getBean("memberRepository") != null);
        assertSame(context.getBean("memberRepository"), context.getBean("memberRepository"));
        assertSame(context.getBean("memberService"), context.getBean("memberService"));
        assertSame(((MemberService) context.getBean("memberService")).getMemberRepository(),
                ((MemberRepository) ((MemberService) context.getBean("memberService")).getMemberRepository()));
    }

    /*
        use parallel in forEach Methods
        none parallel  239 ms
        parallel 238   ms
     */
    @Test
    void parallelTest() {
        new BeanFactory(find("com.core")).getContext(TestConfig.class);
    }

    @Test
    void findAutowiredConstructor() {
        ComponentContext context = (ComponentContext) new ComponentFactory().
                getContext(TestConfig.class);
        assertTrue(context != null);

        context.ifCallAutowiredInstance("MemberServiceImpl");
    }
    @Test
    void factory에서_식별하기(){
        new ComponentFactory();
    }
}


