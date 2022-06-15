# Configuration

1. @Configuration이 붙은 Class는 CGLIB이 해당 클래스를 상속받아서 대리자?? 역활을 수행한다
2. CGLIB은 @Configuration의 해당 Bean들을 모두 호출하면서 해당 Bean들을 컨테이너에 등록한다
3. 만약 나중에 인스턴스를 꺼내려고 하면 AppConfig 이 아니라 CGLIB이 이 요청을 받아서 컨테이너에서 꺼낸다.

## 컨테이너 최대한 구현하기

```java
        Class<?> object=Class.forName("com.core.spring.domain.AppConfig");
        System.out.println("getName >> "+object.getName());
        System.out.println("getSimpleName >> "+object.getSimpleName());
        System.out.println("getPackageName >> "+object.getPackageName());
        System.out.println("getSuperclass >> "+object.getSuperclass().getName());
        System.out.println("getTypeName >> "+object.getTypeName());
        Arrays.stream(object.getDeclaredMethods()).forEach(method->
        System.out.println("mehotd >> "+method));
```

* Class.forName 의 풀패키지경로를 입력해서 해당 클래스 인스턴스를 받아올 수 있다

```text
getName >> com.core.spring.domain.AppConfig
getSimpleName >> AppConfig
getPackageName >> com.core.spring.domain
getSuperclass >> java.lang.Object
getTypeName >> com.core.spring.domain.AppConfig
mehotd >> public com.core.spring.SingletonProblem com.core.spring.domain.AppConfig.singletonProblem()
mehotd >> public com.core.spring.domain.member.MemberRepository com.core.spring.domain.AppConfig.memberRepository()
mehotd >> public com.core.spring.domain.member.MemberService com.core.spring.domain.AppConfig.memberService()
mehotd >> public com.core.spring.domain.order.OrderService com.core.spring.domain.AppConfig.orderService()
```

* Class에 존재하는 Method를 받아서 출력하자

```java
    @Test
    void 받은_메서드_invoke()throws ClassNotFoundException,NoSuchMethodException{
            Class<?> object=Class.forName("com.core.spring.domain.AppConfig");
        Method method=object.getMethod("memberRepository");

        System.out.println(method.getDeclaringClass());
        System.out.println(method.getName());
        System.out.println(method.getParameterTypes());
        System.out.println(method.getReturnType());
        }
```

```text
class com.core.spring.domain.AppConfig
memberRepository
[Ljava.lang.Class;@24ba9639  => 이 클래스 객체가 기본 유형 또는 void를 나타내는 경우 반환된다. 해당 메서드에는 반환형이 없음
interface com.core.spring.domain.member.MemberRepository
```

* 실제 메서드 실행

```java
    @Test
    void 받은_메서드_실행()throws ClassNotFoundException,NoSuchMethodException,InvocationTargetException,IllegalAccessException,InstantiationException{
            Class<?> object=Class.forName("com.core.spring.domain.AppConfig");
        Method method=object.getMethod("memberRepository");
        Object appConfig=object.newInstance();

        MemberRepository repository=(MemberRepository)method.invoke(appConfig);
        assertDoesNotThrow(()->repository.save(new Member(1L,"hojun",Grade.BASIC)));
        }
```

* 이 방법을 이용하면 컨테이너를 만들 수 있지 않을까??

* Annotation을 하나 정의한다
    * 단 @Retention(RetentionPolicy.RUNTIME)를 적용해야지 getAnnotation이 가능하다
    * Retention은 어노테이션 유지 기간을 정하는 설정이다
    * RetentionPolicy는 RUNTIME으로 설정하면 VM에 의해 유지되고, 리플렉션을 통해서 읽을 수 있다.
        * 런타임 종료까지 메모리에 살아있다.
* 모든 클래스의 정보를 받아와서 내가 만든 어노테이션이 클래스에 있는지 확인한다
    * 만약 있다면 메서드들을 실행한다. 그리고 어딘가에 결과를 저장한다

```java
// 모든 클래스를 출력하는 역활  이 코드는 stackoverflow에서 참조했다.
class ClassesLoader {
    private static final char PKG_SEPARATOR = '.';

    private static final char DIR_SEPARATOR = '/';

    private static final String CLASS_FILE_SUFFIX = ".class";

    private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

    //루트 패키지 경로인 "com.core"를 넣어준다
    public static List<Class<?>> find(String scannedPackage) {
        String scannedPath = scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        //현재 스레드에서 resource를 읽어온다. 
        //결과로 com.core의 절대경로가 등록된다 . (E:\~~~~)
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (scannedUrl == null) {
//            throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
        }
        // 절대경로를 읽는다
        File scannedDir = new File(scannedUrl.getFile());
        List<Class<?>> classes = new ArrayList<Class<?>>();
        //디렉터리에 존재하는 모든 파일을 순회한다
        for (File file : scannedDir.listFiles()) {
            classes.addAll(find(file, scannedPackage));
        }
        return classes;
    }

    private static List<Class<?>> find(File file, String scannedPackage) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String resource = scannedPackage + PKG_SEPARATOR + file.getName();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                classes.addAll(find(child, resource));
            }
        } //만약 .class 형식이라면 classes에 더한다
        else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException ignore) {
            }
        }
        return classes;
    }
}
```

* 위의 코드를 실행해서 현재 com.core에 해당하는 모든 클래스 정보를 얻어온다
* 그리고 밑의 과정을 수행해서 함수들을 invoke하여 객체들을 받아온다

```java
@Test
    void 어노테이션에_해당하는_함수들을_모두_호출한다(){
            List<Class<?>>classes=find("com.core");
        List<Object> result=new ArrayList<>();

        classes.stream().forEach(aClass->{
        try{
        Object object=aClass.getDeclaredConstructor().newInstance();
        if(aClass.getDeclaredAnnotationsByType(AnnotationTest.class).length!=0)
        Arrays.stream(aClass.getDeclaredMethods()).filter(method->method.getParameterCount()==0&&
        method.getDeclaredAnnotationsByType(CustomBean.class)!=null)
        .forEach(method->{
        System.out.println("===="+object+"====");
        try{
        result.add(method.invoke(object));
        }catch(IllegalAccessException|InvocationTargetException e){
        }
        });
        }catch(InstantiationException|IllegalAccessException|NoSuchMethodException|InvocationTargetException e){
        }
        });
        }
```

###  테스트

```java
    @Test
    void runMyContainer(){
            InstanceContainer container=new InstanceContainer(Core.makeInstance(AllClassesLoader.find("com.core")));

            assertThrows(NoSuchBeanDefinitionException.class,
        ()->container.getInstance("NOT_EXIST_METHOD"));

        assertTrue(container.getInstance("hello")instanceof String);

        }
```
### bean으로 등록된 클래스를 상속받아서 클래스를 만든다
1. 일단 해당 클래스 상속부터 받아보자 
   1. cglib이 필요하다
      1. target이 된 class를 상속받아야한다
   2. Enhancer => Generates dynamic subclasses to enable method interception. 동적 서브 클래스를 생성한다
2. 
```java
 @Test
    void cglib() {
        InstanceContainer container = new InstanceContainer(Core.makeInstance(find("com.core")));
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(TestConfig.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                if(!container.isExist(method.getName())) {
                    System.out.println("not exits >> " + method.getName() );
                    container.add(method.getName() ,proxy.invokeSuper(obj,args));
                }
                return container.getInstance(method.getName());
            }
        });
        TestConfig testConfig = (TestConfig) enhancer.create();
        System.out.println(testConfig.hello());
        System.out.println(testConfig.world());

        System.out.println(new TestConfig().hello());
    }
```
### 이렇게 해당 인스턴스가 존재하지 않는다면 새로 만들고 컨테이너에 넣어준다
1. https://nhj12311.tistory.com/469
### 다음 문제는 이 클래스를 어떻게 원본 클래스와 바꿀건지 !


### 궁금한거 
1. 만약 애플리케이션 로딩과 동시에 내가 만든 컨테이너가 실행되면 이는 싱글톤이 보장된다
    *. 만약 멀티 스레드 요청이 들어와도 동일 인스턴스를 리턴해서!! 
