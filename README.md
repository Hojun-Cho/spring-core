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
* ApplicationContext를 이용해 확인이 가능하다
    * ApplicationContext는 BeanFactory를 포함하는 컨테이너

```java
    public static void main(String[]args){
        ApplicationContext context=new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println(context.getApplicationName());
        Arrays.stream(context.getBeanDefinitionNames()).forEach(name->System.out.println(name));
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

* 또한 실제 참조주소를 살펴보면 모두 MemberRepository@1819로 같은 주소를 참조하고 있다.

```java
        MemberService memberService=context.getBean("memberService",MemberService.class);
        OrderService orderService=context.getBean("orderService",OrderService.class);
        MemberRepository memberRepository=context.getBean("memberRepository",MemberRepository.class);
```

![img.png](img.png)

* 같은 개체라도 Bean으로 등록된 이름이 다르다면 서로 다른 인스턴스를 return한다.

### Object 타입으로 빈 조회하기

```java
    @Test
    void Bean의_상속관계(){
            Map<String, Object> o=context.getBeansOfType(Object.class);
        o.keySet().stream().forEach(
        key->System.out.println("\n"+key+" >>> "+o.get(key).toString()+"\n"));
        }
```

* 내부적으로 사용하는 bean까지 모두 나온다

```shell
org.springframework.context.annotation.internalConfigurationAnnotationProcessor >>> org.springframework.context.annotation.ConfigurationClassPostProcessor@70f43b45

...

lifecycleProcessor >>> org.springframework.context.support.DefaultLifecycleProcessor@77b325b3

BUILD SUCCESSFUL in 4s
4 actionable tasks: 2 executed, 2 up-to-date
오전 11:02:26: 실행이 완료되었습니다 ':test --tests "com.core.spring.domain.AppTest.Bean의_상속관계"'.
```
* internalConfigurationAnnotationProcessor의 역활 
* postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)를 호출한다 >>   레지스트리의 구성 클래스에서 추가 bean 정의를 가져옵니다.
  * BeanDefinitionRegistry registry에는 현재 Configuration으로 등록된 모든 Class가 존재한다
  * 여기서 getBeanDefinitionNames를 호출해 candidateNames을 가져온다. 
  * 이때 가져온 candidateNames에는 아직 클래스 내부에 정의된 Bean들이 없다
* 가져온 candidateNames으로 
  * else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory))
  * 를 호출해서 configCandidates에 add한다. 아직 클래스에 속한 Bean의 메타데이터를 부르지 않음
* this.reader.loadBeanDefinitions(configClasses);를 호출해서 configuration에 있는 bean metaData를 읽어온다.
* 그러면 registry의 beanDefinitionMap에 해당 Configuration클래스에 속하는 Bean들이 등록된다
* 이렇게 bean의 메타정보를 registry에 등록한다 
* 원래는 Configuration의 어노테이션에 해당하는 클래스 자체만 존재했지만 , 이제는 그 안에 존재하는 Bean들의 메타정보도 읽어서 저장했다.
  * 여기서 registry는 **BeanDefinitionRegistry registry** 이다 


### Singleton
1. 사용자가 호출하지 않아도 Bean으로 등록된 정보들은 스프링이 먼저 초기화를 해준다! .
2. 따라서 몇 번을 호출하던 계속 같은 객체가 리턴