# Custom Annotation Configuration for Spring Remoting

_19 Mar 2009_

[SPR-3926](http://jira.springframework.org/browse/SPR-3926) requests a `@Service` annotation for Spring Remoting configuration, and makes for an interesting foray into Spring's annotation support.

The goal I had in mind in my initial attempt at tackling this was to create an annotation which would allow specification of the common Spring Remoting configuration supporting the four remoting technologies:

* Spring's HTTP invoker (conventional Java serialization)
* Hessian (binary HTTP serialization)
* Burlap (XML HTTP serialization)
* RMI

These translate into the `ServiceType` enumeration, which `@Service` annotated services use to specify their desired remoting technology.

```java
public enum ServiceType {
  HTTP, BURLAP, HESSIAN, RMI
}
```
Spring makes the task easy, as configuration for each of these requires no more than the identification of the service interface.

The `@Service` annotation needs to define only two key pieces of information: the remoting technology to use, and the service interface to expose.

```java
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {

    ServiceType serviceType() default ServiceType.HTTP;

    Class<?> serviceInterface();
}
```

An `InstantiationAwareBeanPostProcessorAdapter` handles interpreting `@Service` annotated classes by instantiating the appropriate `RemoteExporter`, and initializing its serviceInterface and `serviceName` properties.

```java
public class ServiceAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements PriorityOrdered {

    private int order = Ordered.LOWEST_PRECEDENCE - 1;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Service service = AnnotationUtils.findAnnotation(bean.getClass(), Service.class);

        Object resultBean = bean;

        if (null != service) {

            if (ServiceType.HTTP == service.serviceType()) {

                HttpInvokerServiceExporter httpInvokerServiceExporter = new HttpInvokerServiceExporter();
                httpInvokerServiceExporter.setServiceInterface(service.serviceInterface());
                httpInvokerServiceExporter.setService(bean);
                httpInvokerServiceExporter.afterPropertiesSet();
                resultBean = httpInvokerServiceExporter;

            } else if (ServiceType.HESSIAN == service.serviceType()) {

                HessianServiceExporter hessianServiceExporter = new HessianServiceExporter();
                hessianServiceExporter.setServiceInterface(service.serviceInterface());
                hessianServiceExporter.setService(bean);
                hessianServiceExporter.afterPropertiesSet();
                resultBean = hessianServiceExporter;

            } else if (ServiceType.BURLAP == service.serviceType()) {

                BurlapServiceExporter burlapServiceExporter = new BurlapServiceExporter();
                burlapServiceExporter.setServiceInterface(service.serviceInterface());
                burlapServiceExporter.setService(bean);
                burlapServiceExporter.afterPropertiesSet();
                resultBean = burlapServiceExporter;

            } else if (ServiceType.RMI == service.serviceType()) {

                RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
                rmiServiceExporter.setServiceInterface(service.serviceInterface());
                rmiServiceExporter.setService(bean);
                rmiServiceExporter.setServiceName(beanName);
                try {
                    rmiServiceExporter.afterPropertiesSet();
                } catch (RemoteException remoteException) {
                    throw new FatalBeanException("Exception initializing RmiServiceExporter", remoteException);
                }
                resultBean = rmiServiceExporter;
            }
        }

        return resultBean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
```

The `ServiceAnnotationBeanPostProcessor` must be added to the appropriate bean definition registry, which in this case occurs in `AnnotationConfigUtils#registerAnnotationConfigProcessors`.

```java
if (!registry.containsBeanDefinition(SERVICE_ANNOTATION_PROCESSOR_BEAN_NAME)) {
    RootBeanDefinition def = new RootBeanDefinition(ServiceAnnotationBeanPostProcessor.class);
    def.setSource(source);
    def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    beanDefs.add(registerPostProcessor(registry, def, SERVICE_ANNOTATION_PROCESSOR_BEAN_NAME));
}
```

That's all there is to it. To take the new `@Service` annotation for a test run, a test service interface is defined, and a test implementation is written and annotated.

```java
public interface DateService {
    public Date getDate();
}
```

```java
@Service(serviceInterface = DateService.class, serviceType = ServiceType.HTTP)
public class DateServiceImpl implements DateService {

    @Override
    public Date getDate() {
        return new Date();
    }
}
```

`DateServiceImpl` uses the `@Service` annotation to identify `DateService` as its service interface, and Spring's HTTP invoker as the remoting technology. The Spring configuration on the server side is now quite simple.

```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:context="http://www.springframework.org/schema/context"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
                      http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.1.xsd">

  <context:annotation-config />

  <bean id="/DateService" class="com.earldouglas.springremoting.DateServiceImpl" />

</beans>
```

The service is linked in at the client in the usual way, and neither the client nor the server's application context notice the difference.

It would be interesting and probably worthwhile to explore JAX-RPC, JAX-WS, and JMS in addition the four remoting technologies already supported by the new `@Service` annotation, and may make for a future post.

As usual, the Spring Reference is a great resource for learning more. 