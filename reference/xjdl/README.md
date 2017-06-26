# About

XJDL (an eXtensible Java Declarative Licensing framework) is a lightweight, non-intrusive framework designed to integrate licensing into Java projects with minimum development effort.

# Declarative licensing

XJDL uses its `@Licensed` annotation to mark domain services as licensed features, marking methods as candidates for license authorization. License authorization functionality is transparently wired into the applicable domain services via aspect-oriented weaving, and when a licensed feature is used in the absence of appropriate authorization it throws a licensing exception, optionally generates log and alert messages.

# Features

* Common license types provided out of the box
* Enable/disable, time-boxed, usage count
* Support for adding custom license types
* Common license loading mechanisms out of the box
* File, classpath, JDBC, property, direct
* Support for adding custom license loading mechanisms.
* Hot-swapping of installed licenses

# Example: Spring AOP

`SampleService.java:`

    public interface SampleService {
      public int add(int number1, int number2);
    }

`SampleServiceImpl.java:`

    public class SampleServiceImpl implements SampleService {
      @Licensed
      public int add(int number1, int number2) {
          return number1 + number2;
      }
    }

`applicationContext.xml:`

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:aop="http://www.springframework.org/schema/aop"
      xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
        http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.0.xsd">
      <aop:aspectj-autoproxy />
      <bean class="com.earldouglas.xjdl.LicenseManager">
        <property name="licenseLoader">
          <bean class="com.earldouglas.xjdl.io.StringLicenseLoader">
            <property name="key" value="encrypt!encrypt!" />
            <property name="license" value="XJ7d9NHF/p6sbyAmtGyiB5ggIYD6WM3VxAjTrmrG4ae00hovgxXXUPLlM9my9wJ58ogcf8LMkpGXXxYwlyUnWg==" />
          </bean>
        </property>
      </bean>
      <bean class="com.earldouglas.test.service.SampleServiceImpl" />
    </beans>

