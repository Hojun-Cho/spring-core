package com.core.spring;

import com.core.spring.customDI.InstanceContainer;
import com.core.spring.domain.AppConfig;
import com.core.spring.domain.member.MemberRepository;
import com.core.spring.domain.member.MemberService;
import com.core.spring.domain.order.OrderService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.Map;

import static com.core.spring.customDI.AllClassesLoader.find;

public class App {

//    public static void main(String[] args) {
//      InstanceContainer container =  new   InstanceContainer(Core.makeInstance(find("com.core")));
//      Map result= container.getInstances();
//    }
}
