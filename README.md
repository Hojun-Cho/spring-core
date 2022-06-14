# configuration

* 해당 어노테이션이 붙은 Class에 선언된 @Bean이 붙은 메서드를 *런타임*에 해당 Bean을 가져온다.
    * 가져오는 방법은 *스프링 컨테이너*에 요청한다.
* AnnotationConfigApplicationContext를 이용하요 BootStraping이 가능하다
    * 여기서 BootStraping은 스프링 컨테이너에 등록된 정보들을 꺼내오는 초기작업을 말한다
* @Configuration은 @Component로 처리된다.
    * 따라서 @Configuration 클래스는 Component Scan의 대상이므로 @Autowirted를 사용할 수 있다.
    * 일반 @Component처럼 주입이 가능하다

```java
    // SomeBean처럼 final 선언된 인스턴스의 경우 SomeBean의 유일성을 스프링이 보장한다
// 단 SomeBean을 생성하는게 AppConfig에만 존재하는 경우 
@Configuration
public class AppConfig {

    private final SomeBean someBean;

    public AppConfig(SomeBean someBean) {
        this.someBean = someBean;
    }

    // @Bean definition using "SomeBean"

}
```                                 

```java

@Configuration
public class AppConfig {
    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository()
                , new RateDiscountPolicy());
    }
}
```

* 위의 class에 정의된 Bean들이 스프링컨테이너에 등록되고

```java
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println(context.getApplicationName());
        Arrays.stream(context.getBeanDefinitionNames()).forEach(name-> System.out.println(name));
    }
```
* 해당 클래스(AppConfig)에서 Bean으로 등록된 정보들을 조회가 가능하다
```shell
org.springframework.context.annotation.internalConfigurationAnnotationProcessor
org.springframework.context.annotation.internalAutowiredAnnotationProcessor
org.springframework.context.annotation.internalCommonAnnotationProcessor
org.springframework.context.event.internalEventListenerProcessor
org.springframework.context.event.internalEventListenerFactory

내가 정의한 정보들
appConfig  클래스 이름이 AppConfig이다. 
memberRepository
memberService
orderService
```
* appconfig은 내가 작성한 클래스이다. 그런데 왜 bean으로 등록된걸까??
  * @Configuration은 @Component로 처리된다. 따라서 AppConfig도 하나의 Bean으로 등록되었다
* 스프링 컨테이너가 관리하는 Bean들은 기본적으로 singleton이다
  * 실제 isSingleton 메서를 호출하면 모두 true로 나온다
```shell
org.springframework.context.annotation.internalConfigurationAnnotationProcessor > true
org.springframework.context.annotation.internalAutowiredAnnotationProcessor > true
org.springframework.context.annotation.internalCommonAnnotationProcessor > true
org.springframework.context.event.internalEventListenerProcessor > true
org.springframework.context.event.internalEventListenerFactory > true
appConfig > true
memberRepository > true
memberService > true
orderService > true
```
