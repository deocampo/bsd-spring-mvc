# Simple Remoting with Spring

_16 Mar 2009_

Remote access to the business logic of a J2EE web application is often a useful capability, though it can be time-consuming to implement. Spring Remoting takes nearly all of the confusion out of exposing services over the web for Spring clients to consume as easily as though they were locally instantiated. Commonly, Spring Remoting is used to expose a black-boxed implementation of a published interface over HTTP, with all communication between the client and server abstracted behind Spring libraries, and with data serialized between the client and server using simple Java serialization.

This tutorial will walk through a very simple Spring Remoting application, explaining some of the high-level concepts behind setting up a Spring Remoting web application to expose a service, and setting up a Spring client application which consumes the Spring Remoting service. Spring 3.1 is used.

The service will provide the date/time of the remote server with a basic `java.util.Date` object. The service interface is defined as `DateService`, and implemented as `DateServiceImpl`.

```java
package com.earldouglas.springremoting;

import java.util.Date;

public interface DateService {
  public Date getDate();
}

package com.earldouglas.springremoting;

import java.util.Date;

public class DateServiceImpl implements DateService {

  @Override
  public Date getDate() {
    return new Date();
  }
}
```

The server's Spring context, defined in `WEB-INF/remoting-servlet.xml`, instantiates a `DateServiceImpl` and exposes it through Spring's `HttpInvokerServiceExporter`.

```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd">

  <bean id="dateService" class="com.earldouglas.springremoting.DateServiceImpl" />

  <bean name="/DateService" class="org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter">
    <property name="service" ref="dateService" />
    <property name="serviceInterface" value="com.earldouglas.springremoting.DateService" />
  </bean>

</beans>
```

The server's deployment descriptor, defined in `WEB-INF/web.xml`, utilizes Spring's `DispatcherServlet` to route requests to the `HttpInvokerServiceExporter`.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app id="WebApp_ID" version="2.4"
  xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">

  <display-name>Spring Remoting</display-name>

  <servlet>
    <servlet-name>remoting</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
  </servlet>

  <servlet-mapping>
    <servlet-name>remoting</servlet-name>
    <url-pattern>/*</url-pattern>
  </servlet-mapping>

</web-app>
```

That's all there is to setting up a service with Spring Remoting. Note that no Spring-specific code must be written, and Spring-specific configuration is minimal.

From the client's perspective, it is expected to see an exposed `DateService` providing a `getDate()` method. The implementation of the `DateService` is inconsequential to the client application, which only cares about the interface.

The client application links in the service using Spring's `HttpInvokerProxyFactoryBean`. This creates a proxy object which looks to the rest of the application like a local implementation of `DateService`, but all interaction with it is actually happening over the serialized channel between the client's Spring context and the server's Spring context.

```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd">

  <bean id="dateService" class="org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean">
    <property name="serviceUrl" value="http://localhost:8080/Spring_Remoting/DateService" />
    <property name="serviceInterface" value="com.earldouglas.springremoting.DateService" />
  </bean>

</beans>
```

The client application is a simple JUnit test which uses `SpringJUnit4ClassRunner` to set up a Spring context and wire in the remote `DateService` as a local variable.

```java
package com.earldouglas.springremoting;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class DateServiceTest {

  @Autowired
  private DateService dateService;

  @Test
  public void testDateService() {
    System.out.println(dateService.getDate());
  }
}
```

When the client application runs `dateService.getDate()`, Spring is actually sending the method call to the remote `DateServiceImpl` on the J2EE server. The method executes on the server, and the result is sent back via the exposed `DateService` to the client application which receives the result as though it were run locally.

As with the server, there is no need for Spring-specific code, and there is minimal Spring-specific configuration. A Spring-enabled JUnit test was used to take advantage of automatic application context creation and autowiring of the bean in the application context.

It is also possible to utilize Spring remoting in non-Spring client applications by replicating the `HttpInvokerProxyFactoryBean` initialization within the client application code.

```xml
package com.earldouglas.springremoting;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

public class NonSpringDateServiceTest extends TestCase {

  private DateService dateService;

  @Override
  public void setUp() throws Exception {
    HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();
    httpInvokerProxyFactoryBean.setServiceInterface(DateService.class);
    httpInvokerProxyFactoryBean.setServiceUrl("http://localhost:8080/Spring_Remoting/DateService");
    httpInvokerProxyFactoryBean.afterPropertiesSet();
    this.dateService = (DateService) httpInvokerProxyFactoryBean.getObject();
  }

  @Test
  public void testDateService() {
    System.out.println(dateService.getDate());
  }
}
```

The code implemented in the `setUp()` method has the same effect as the Spring context otherwise would in a Spring-enabled application, but without the need for such.

When services are exposed over the web, security becomes an important consideration. Until a security layer is overlaid on the Spring Remoting capability, it lies vulnerable to invocation by third parties.

For more information, the [Spring Reference](http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/remoting.html) is a fantastic guide with a great deal of in-depth coverage of Spring Remoting. 
