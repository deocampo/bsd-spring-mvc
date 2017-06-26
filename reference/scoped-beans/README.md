# Scoped Bean Dependencies

_4 Jan 2011_

Spring provides multiple [bean scopes](http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/beans.html#beans-factory-scopes), and though singleton is predominant it is sometimes useful to inject non-singleton (and non-prototype) dependencies

In this short example, I show how to dependency inject a [request-scoped bean](http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/beans.html#beans-factory-scopes-request) into a Spring MVC controller, a [singleton bean](http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/beans.html#beans-factory-scopes-singleton).

Consider the following controller, which has a single dependency of type Metadata.

_MetadataController.java:_

```java
@Controller
public class MetadataController {

  @Autowired
  private Metadata metadata;

  @RequestMapping(value = "/metadata")
  public void metadata(Model model) {
    model.addAttribute("date", metadata.getDate());
  }
}
```

The `Metadata` and a simple implementation allow storage and retrieval of a Date.

_Metadata.java:_

```java
public interface Metadata {
  public String getDate();
}
```

_MetadataImpl.java:_

```java
public class MetadataImpl implements Metadata {

  private final Date date;

  public MetadataImpl(Date date) {
    this.date = date;
  }

  @Override
  public String getDate() {
    return date.toString();
  }
}
```

These beans are wired into a basic Spring MVC application.

_spring-mvc-servlet.xml:_

```xml
<mvc:annotation-driven />

<bean
  class="org.springframework.web.servlet.view.InternalResourceViewResolver">
  <property name="prefix" value="/WEB-INF/jsp/" />
  <property name="suffix" value=".jsp" />
</bean>

<context:component-scan base-package="com.earldouglas.requestscope" />

<context:annotation-config />

<bean class="com.earldouglas.requestscope.MetadataImpl" scope="request">
  <constructor-arg>
    <bean class="java.util.Date" />
  </constructor-arg>
  <aop:scoped-proxy proxy-target-class="false" />
</bean>
```

The only things out of the ordinary here are the scope of the `MetadataImpl` bean, which is set to request, and the [`<aop:scoped-proxy />`](http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/beans.html#beans-factory-scopes-other-injection) element within it.

The `request` scope tells Spring to instantiate a new `MetadataImpl` bean with each Web request, and to make that bean available only within the context of that Web request.

The `<aop:scoped-proxy />` element tells Spring that this bean is not to be injected as a dependency directly, but instead an AOP proxy of this bean is to be injected in its place. This allows Spring to make each request-scoped `MetadataImpl` dependency available to the Web request thread to which it corresponds. The [`proxy-target-class="false"`](http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/beans.html#beans-factory-scopes-other-injection-proxies) attribute tells Spring not to use a [CGLIB proxy](http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/aop-api.html#aop-pfb-proxy-types), but a JDK dynamic proxy instead.

A simple view displays the Date, which is created for each request the controller receives.

_metadata.jsp:_

```xml
<h1>Metadata</h1>
Date: <c:out value="${date}" />
```
