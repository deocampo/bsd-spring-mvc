# Custom Annotation Configuration for Spring Remoting, Part Two

_13 Jun 2009_

In my [first attempt](https://github.com/JamesEarlDouglas/spring-remoting-annotation) at [SPR-3926](http://jira.springframework.org/browse/SPR-3926), I ended up with a `@Service` annotation which was wholly separate from Spring's existing `@Service` annotation, and required either a confusing coexistence or an inappropriate integration of the two. It also required implementations of service interfaces to be annotated as Spring Remoting services, which is arguably less intuitive than annotating the service interfaces themselves as Spring Remoting services.

After some very helpful advice from Chris Beams, I have reworked most of the original code. To avoid stepping all over the intended function of the existing `@Service` annotation, I created the `@Remote` annotation:

```java
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Remote {

    Class<?> serviceExporter() default HttpInvokerServiceExporter.class;
}
```

To use the new annotation, a service interface is annotated with `@Remote`, optionally specifying `serviceExporter`. The optional `serviceExporter` may be any of the `HttpInvokerServiceExporterRmiServiceExporter`, `HessianServiceExporter`, or `BurlapServiceExporter` classes, and defaults to `HttpInvokerServiceExporter` if not specified. The reason I chose to allow direct specification of the `RemoteExporter` class as the `serviceExporter` is so future implementations of remoting technologies (JSON, etc.) will be supported without modification to the `@Remote` definition. The alternative to this is to maintain the previous pattern of an enumerated type which is later mapped to corresponding `RemoteExporter` implementations.

An implementation of the service interface then simply needs to be annotated with `@Service`, so it will be picked up by `<context:component-scan />` during application context initialization.

A new element definition named `remote-export` is needed in [`spring-context-3.1.xsd`](http://www.springframework.org/schema/context/spring-context-3.1.xsd), which is used by adding `<context:remote-export />` to the context configuration:

```java
<xsd:element name="remote-export">
  <xsd:annotation>
    <xsd:documentation><![CDATA[
  Exports classes whose interfaces have been annotated with @Remote as Spring Remoting services.
    ]]></xsd:documentation>
  </xsd:annotation>
</xsd:element>
```

To handle the new `remote-export` element, I created `RemoteBeanDefinitionParser` and added it to `ContextNamespaceHandler.init()`:

```java
public void init() {
    ...
    registerBeanDefinitionParser("remote-export", new RemoteBeanDefinitionParser());
}
```

```java
public BeanDefinition parse(Element element, ParserContext parserContext) {
    RootBeanDefinition bd = new RootBeanDefinition(RemotingExporter.class);
    bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    bd.setSource(parserContext.extractSource(element));
    parserContext.registerBeanComponent(new BeanComponentDefinition(bd, RemotingExporter.class.getName()));
    return null;
}
```

`RemoteBeanDefinitionParser` registers a `RemotingExporter`, which looks for `@Remote` beans in the context, and wires them up with the appropriate service exporters:

```java
public class RemotingExporter implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            Object bean = beanFactory.getBean(beanName);
            Class<?> serviceInterface = findServiceInterface(bean);
            if (serviceInterface != null) {
                RemoteExporter remoteExporter = createRemoteExporter(serviceInterface, bean);
                beanFactory.registerSingleton("/" + serviceInterface.getName(), remoteExporter);
            }
        }
    }

    private Class<?> findServiceInterface(Object service) {
        Class<?> serviceInterface = null;
        if (AnnotationUtils.isAnnotationDeclaredLocally(Service.class, service.getClass())) {
            for (Class<?> interfaceClass : service.getClass().getInterfaces()) {
                if (AnnotationUtils.isAnnotationDeclaredLocally(Remote.class, interfaceClass)) {
                    serviceInterface = interfaceClass;
                }
            }
        }
        return serviceInterface;
    }

    private RemoteExporter createRemoteExporter(Class<?> serviceInterface, Object service) {
        RemoteExporter remoteExporter = null;
        Remote remote = AnnotationUtils.findAnnotation(service.getClass(), Remote.class);
        try {
            remoteExporter = (RemoteExporter) remote.serviceExporter().newInstance();
            remoteExporter.setService(service);
            remoteExporter.setServiceInterface(serviceInterface);
            if (remoteExporter instanceof RmiServiceExporter) {
                ((RmiServiceExporter) remoteExporter).setServiceName(serviceInterface.getName());
            }
            if (remoteExporter instanceof InitializingBean) {
                ((InitializingBean) remoteExporter).afterPropertiesSet();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return remoteExporter;
    }
}
```

In short, a class which implements a `@Remote`-annotated interface may be instantiated and exposed as a Spring Remoting service by including only two lines in the context configuration:

```xml
<context:remote-export />
<context:component-scan base-package="path.to.service.annotated.implementation" />
```

There are trade-offs in both this and my previous method, but with the architectural improvements here it would be hard to argue going back. 
