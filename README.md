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

* ConfigurationClassPostProcessor
    * 이 포스트 프로세서는 @Bean 메서드가 @Configuration 클래스에 선언되기 전에
    * 해당 빈 정의가 등록되어 있어야 하므로 우선순위가 지정됩니다.
        * @Configuration에서 이용하기 위해 Bean을 정의한다
    * processConfigBeanDefinitions 메서드를 살펴보자
        * Build and validate a configuration model based on the registry of Configuration classes
        * configuration클래스를 기반으로 @Configuration이 붙은 Class 안에 정의된 빈을 등록하는 메서드이다
        * 이 메서드 호출 전에는 아직 사용자 정의 빈이 @Configuration만 등록되어있다.
        * ![img_1.png](img_1.png)
      ```java
		List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
		String[] candidateNames = registry.getBeanDefinitionNames();

		for (String beanName : candidateNames) {
			BeanDefinition beanDef = registry.getBeanDefinition(beanName);
			if (beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE) != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
				}
			}
			else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
				configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
  }
    
```
    * candidateNames안에 스프링에 등록된 Bean들이 존재하고 for loop를 돌면서 각각의 Configuration에 등록된 세부 Bean들을 읽는다 
    * checkConfigurationClassCandidate에 들어가서 configCandidates에 add 된다.
    * 해당 클래스에 설정된 bean method를 읽어들여 Bean들을 정의한다.
