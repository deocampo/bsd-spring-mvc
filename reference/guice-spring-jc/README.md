# Comparing Guice with Spring JavaConfig

_25 Aug 2009_

From a high level, Guice and Spring JavaConfig bring many of the same capabilities to the table. They both provide annotation-based dependency injection, they both boast simple APIs, and they both get you quickly up and running with an IoC container.

Spring JavaConfig mainly brings a new type of `ApplicationContext` into the picture, namely `AnnotationConfigApplicationContext`, along with various support infrastructure including new annotations such as `@Bean`, `@Configuration`, etc. Otherwise, it's still the same familiar Spring.

Guice follows a different albeit similar model, introducing non-Spring infrastructure including annotations, support classes, etc. The only scent of Spring hovers around the `SpringIntegration` class.

Comparing the two side-by-side exhibits their similarities as dependency injection frameworks, and exposes their differences as Spring-integrable modules. This comparison includes a single interface and implementation, called `Greeter` and `DefaultGreeter`, which define and realize a simple function called `getGreeting()` which returns the obligatory `"Hello World!"`.

```java
public class DefaultGreeter implements Greeter {

  public String getGreeting() {
    return "Hello World!";
  }
}
```

The Guice `Module`, an analog to a Spring context configuration, simply binds the `Greeter` interface to the only implementation of `DefaultGreeter`.

```java
public class SimpleGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Greeter.class).to(DefaultGreeter.class);
    }
}
```

`SimpleGuiceModule` is bootstrapped by creating an `Injector`. The bound `Greeter` is retrieved, and its greeting is printed.

```java
public class SimpleGuiceBootstrapper {

    public static void main(String[] arguments) {
        Injector injector = Guice.createInjector(new SimpleGuiceModule());
        Greeter greeter = injector.getInstance(Greeter.class);
        System.out.println(greeter.getGreeting());
    }
}
```

The Spring JavaConfig `Configuration` defines a `@Bean` method which returns an instance of `DefaultGreeter`.

```java
@Configuration
public class SimpleSpringConfiguration {

    @Bean
    public Greeter greeter() {
        return new DefaultGreeter();
    }
}
```

`SimpleSpringConfiguration` is bootstrapped by instantiating a `AnnotationConfigApplicationContext`. The `Greeter` bean is retrieved, and its greeting is printed.

```java
public class SimpleSpringBootstrapper {

    public static void main(String[] arguments) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SimpleSpringConfiguration.class);
        Greeter greeter = applicationContext.getBean(Greeter.class);
        System.out.println(greeter.getGreeting());
    }
}
```

So far the configuration, bootstrapping, and interaction with both Guice and Spring JavaConfig are quite similar in complexity and nearly identical in function. Introducing a schema-based Spring context configuration will fix that.

An XML configuration which represents both the `SimpleGuiceModule` and the `SimpleSpringConfiguration` simply defines a single bean, greeter, as an instance of `DefaultGreeter`.

_config.xml:_

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="
    http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd">

  <bean id="greeter" class="com.earldouglas.guicespringjc.DefaultGreeter" />

</beans>
```

The Guice `Module` binds the beans in Spring configuration using `SpringIntegration`.

```java
public class XmlGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("com/earldouglas/guicespringjc/spring/config.xml");
        SpringIntegration.bindAll(binder(), applicationContext);
    }
}
```

`XmlGuiceModule` is bootstrapped in nearly the same way as was `SimpleGuiceModule`. The difference is that the `Greeter` is retrieved by its implementation class rather than the `Greeter` interface.

```java
public class XmlGuiceBootstrapper {

    public static void main(String[] arguments) {
        Injector injector = Guice.createInjector(new XmlGuiceModule());
        Greeter greeter = injector.getInstance(DefaultGreeter.class);
        System.out.println(greeter.getGreeting());
    }
}
```

The Spring JavaConfig Configuration simply leverages the `@ImportResource` annotation to bind the beans in Spring configuration.

```java
@Configuration
@ImportResource("classpath:com/earldouglas/guicespringjc/spring/config.xml")
public class XmlSpringConfiguration {
}
```

`XmlSpringConfiguration` is bootstrapped exactly as was `SimpleSpringConfiguration`.

```java
public class XmlSpringBootstrapper {

    public static void main(String[] arguments) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(XmlSpringConfiguration.class);
        Greeter greeter = applicationContext.getBean(Greeter.class);
        System.out.println(greeter.getGreeting());
    }
}
```

Guice certainly plays well with Spring, but not quite as well as Spring itself. 