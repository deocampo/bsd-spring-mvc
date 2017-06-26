#A Contract-First Web Service with Spring-WS

_13 Sep 2009_

Contract-first web services epitomise the pattern of loose coupling as applied to distributed web-based systems. The methodology is founded on the establishment of a service contract, which both client and server entities agree to implement. With the service contract in place, client and server entities have the freedom to implement it in any way they like, with all functionality abstracted from one another behind the service contract. This is particularly useful when either or both environments change, which could mean anything from minor internal API evolution to major environmental migrations. These changes are generally not perceived by other entities, and certainly can not impact any components of the service contract.

Spring Web Services (Spring-WS) provides a convenient and robust framework for building contract-first web services, and allows considerable freedom in the configuration and implementation details. This freedom comes with a price, in that there is a wealth of options to learn, and paths from which to choose. This example will explore one such path, and provide a basic tutorial on establishing a simple contract-first web service using Spring-WS. The web service will provide a basic directory service allowing lookup of employees by id.

The first step in building a contract-first web service is to establish the service contract, commonly implemented in WSDL. To begin, a sample XML message is defined which represents the type of messages expected to be processed by the web service. There are two steps to a web service: the request and the response, so two sample XML messages will be defined.

The request simply passes along the id of an employee to be looked up.

_request.xml:_

```xml
<employee-request xmlns="http://earldouglas.com/directory">
  <id>3</id>
</employee-request>
```

The response contains information about the requested employee.

_response.xml:_

```xml
<employee-response xmlns="http://earldouglas.com/directory">
   <employee>
    <id>3</id>
    <name>Johnny McDoe</name>
    <title>Work Man</title>
  </employee>
</employee-response>
```

With the sample messages defined, the data contract can be reverse-engineered. This is done using Trang, a handy tool which takes schema input of one format, and outputs it as another. For the data contract, the input are the sample messages XML and the output is an W3C XML Schema (`.xsd`).

```bash
java -jar trang.jar request.xml response.xml directory-generated.xsd
```

_directory-generated.xsd:_

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
  elementFormDefault="qualified" targetNamespace="http://earldouglas.com/directory"
  xmlns:directory="http://earldouglas.com/directory">
  <xs:element name="employee-request" type="directory:id" />
  <xs:element name="employee-response">
    <xs:complexType>
      <xs:sequence>
        <xs:element ref="directory:employee" />
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="employee">
    <xs:complexType>
      <xs:complexContent>
        <xs:extension base="directory:id">
          <xs:sequence>
            <xs:element ref="directory:name" />
            <xs:element ref="directory:title" />
          </xs:sequence>
        </xs:extension>
      </xs:complexContent>
    </xs:complexType>
  </xs:element>
  <xs:element name="name" type="xs:string" />
  <xs:element name="title" type="xs:string" />
  <xs:complexType name="id">
    <xs:sequence>
      <xs:element ref="directory:id" />
    </xs:sequence>
  </xs:complexType>
  <xs:element name="id" type="xs:integer" />
</xs:schema>
```

Most likely the generated schema will not be exactly as desired for the data contract, and will need to be tweaked by hand. This is the case above, so the schema is modified as follows:

_directory.xsd:_

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
  elementFormDefault="qualified" targetNamespace="http://earldouglas.com/directory"
  xmlns:directory="http://earldouglas.com/directory">
  <xs:element name="employee-request" type="directory:id" />
  <xs:element name="employee-response" type="directory:employee" />
  <xs:complexType name="employee">
    <xs:complexContent>
      <xs:extension base="directory:id">
        <xs:sequence minOccurs="1" maxOccurs="1">
          <xs:element ref="directory:name" />
          <xs:element ref="directory:title" />
        </xs:sequence>
      </xs:extension>
    </xs:complexContent>
  </xs:complexType>
  <xs:element name="name" type="xs:string" />
  <xs:element name="title" type="xs:string" />
  <xs:complexType name="id">
    <xs:sequence minOccurs="1" maxOccurs="1">
      <xs:element ref="directory:id" />
    </xs:sequence>
  </xs:complexType>
  <xs:element name="id" type="xs:integer" />
</xs:schema>
```

With the schema cleaned up and settled into a steady state, the service contract is ready to be defined. This may be done by hand, but is more conveniently done with a tool such as Eclipse or Spring's `DefaultWsdl11Definition`.

_directory.wsdl:_

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
  xmlns:sch="http://earldouglas.com/directory" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
  xmlns:tns="http://earldouglas.com/directory" targetNamespace="http://earldouglas.com/directory">
  <wsdl:types>
    <xs:schema xmlns:directory="http://earldouglas.com/directory"
      xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified"
      targetNamespace="http://earldouglas.com/directory">
      <xs:element name="employee-request" type="directory:id" />
      <xs:element name="employee-response" type="directory:employee" />
      <xs:complexType name="employee">
        <xs:complexContent>
          <xs:extension base="directory:id">
            <xs:sequence maxOccurs="1" minOccurs="1">
              <xs:element ref="directory:name" />
              <xs:element ref="directory:title" />
            </xs:sequence>
          </xs:extension>
        </xs:complexContent>
      </xs:complexType>
      <xs:element name="name" type="xs:string" />
      <xs:element name="title" type="xs:string" />
      <xs:complexType name="id">
        <xs:sequence maxOccurs="1" minOccurs="1">
          <xs:element ref="directory:id" />
        </xs:sequence>
      </xs:complexType>
      <xs:element name="id" type="xs:integer" />
    </xs:schema>
  </wsdl:types>
  <wsdl:message name="employee-response">
    <wsdl:part element="tns:employee-response" name="employee-response">
    </wsdl:part>
  </wsdl:message>
  <wsdl:message name="employee-request">
    <wsdl:part element="tns:employee-request" name="employee-request">
    </wsdl:part>
  </wsdl:message>
  <wsdl:portType name="Directory">
    <wsdl:operation name="employee">
      <wsdl:input message="tns:employee-request" name="employee-request">
      </wsdl:input>
      <wsdl:output message="tns:employee-response" name="employee-response">
      </wsdl:output>
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:binding name="DirectorySoap11" type="tns:Directory">
    <soap:binding style="document"
      transport="http://schemas.xmlsoap.org/soap/http" />
    <wsdl:operation name="employee">
      <soap:operation soapAction="" />
      <wsdl:input name="employee-request">
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output name="employee-response">
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:service name="DirectoryService">
    <wsdl:port binding="tns:DirectorySoap11" name="DirectorySoap11">
      <soap:address location="http://localhost:8080/springws/" />
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>
```

The Java code to represent the data contract within the web service tier is generated with JAXB 2.0, a Java/XML marshalling/unmarshalling framework.

```bash
xjc.sh -p com.earldouglas.directory directory.xsd 
```

With the help of `xjc`, the data contract is used to generate the following Java code, having been conveniently packaged in the `com.earldouglas.directory` package with the `-p` argument.

```java
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "employee", propOrder = {
  "name",
  "title"
})
@XmlRootElement(name = "employee")
public class Employee
  extends Id
{

  @XmlElement(required = true)
  protected String name;
  @XmlElement(required = true)
  protected String title;

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String value) {
    this.title = value;
  }
}
```

```java
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "id", propOrder = {
  "id"
})
@XmlSeeAlso({
  Employee.class
})
@XmlRootElement(name = "id")
public class Id {

  @XmlElement(required = true)
  protected BigInteger id;

  public BigInteger getId() {
    return id;
  }

  public void setId(BigInteger value) {
    this.id = value;
  }
}
```

```java
@XmlRegistry
public class ObjectFactory {

  private final static QName _Name_QNAME = new QName("http://earldouglas.com/directory", "name");
  private final static QName _EmployeeResponse_QNAME = new QName("http://earldouglas.com/directory", "employee-response");
  private final static QName _Title_QNAME = new QName("http://earldouglas.com/directory", "title");
  private final static QName _EmployeeRequest_QNAME = new QName("http://earldouglas.com/directory", "employee-request");
  private final static QName _Id_QNAME = new QName("http://earldouglas.com/directory", "id");

  public ObjectFactory() {
  }

  public Id createId() {
    return new Id();
  }

  public Employee createEmployee() {
    return new Employee();
  }

  @XmlElementDecl(namespace = "http://earldouglas.com/directory", name = "name")
  public JAXBElement<String> createName(String value) {
    return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
  }

  @XmlElementDecl(namespace = "http://earldouglas.com/directory", name = "employee-response")
  public JAXBElement<Employee> createEmployeeResponse(Employee value) {
    return new JAXBElement<Employee>(_EmployeeResponse_QNAME, Employee.class, null, value);
  }

  @XmlElementDecl(namespace = "http://earldouglas.com/directory", name = "title")
  public JAXBElement<String> createTitle(String value) {
    return new JAXBElement<String>(_Title_QNAME, String.class, null, value);
  }

  @XmlElementDecl(namespace = "http://earldouglas.com/directory", name = "employee-request")
  public JAXBElement<Id> createEmployeeRequest(Id value) {
    return new JAXBElement<Id>(_EmployeeRequest_QNAME, Id.class, null, value);
  }

  @XmlElementDecl(namespace = "http://earldouglas.com/directory", name = "id")
  public JAXBElement<BigInteger> createId(BigInteger value) {
    return new JAXBElement<BigInteger>(_Id_QNAME, BigInteger.class, null, value);
  }
}
```

The web service endpoint is defined, taking advantage of Spring-WS's `@Endpoint` and `@PayloadRoot` annotations.

```java
@Endpoint
public class DirectoryEndpoint {

  private Map<BigInteger, Employee> employees;

  public void setEmployees(Map<BigInteger, Employee> employees) {
    this.employees = employees;
  }

  @PayloadRoot(localPart = "employee-request", namespace = "http://earldouglas.com/directory")
  public Employee getEmployees(JAXBElement<Id> idElement) {
    return employees.get(idElement.getValue().getId());
  }
}
```

Deviating slightly from the normal `DispatcherServlet`, the web application deployment descriptor instead uses Spring-WS's `MessageDispatcherServlet`.

_web.xml:_

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app id="WebApp_ID" version="2.4"
  xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">

  <display-name>Directory Services</display-name>

  <servlet>
    <servlet-name>spring-ws</servlet-name>
    <servlet-class>org.springframework.ws.transport.http.MessageDispatcherServlet</servlet-class>
  </servlet>

  <servlet-mapping>
    <servlet-name>spring-ws</servlet-name>
    <url-pattern>/*</url-pattern>
  </servlet-mapping>

</web-app>
```

The Spring context configuration establishes an instance of the endpoint and populates it with some sample `Employee` instances. It sets up a JAXB marshaller utilizing the `oxm` tag library and wires it into an `EndpointAdapter`. Finally an `EndpointMapping` maps the `@PayloadRoot` annotations of the `Endpoint`.

_spring-ws-servlet.xml:_

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:tx="http://www.springframework.org/schema/tx"
  xmlns:util="http://www.springframework.org/schema/util" xmlns:context="http://www.springframework.org/schema/context"
  xmlns:oxm="http://www.springframework.org/schema/oxm"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
  http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd
  http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd
  http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
  http://www.springframework.org/schema/oxm http://www.springframework.org/schema/oxm/spring-oxm-1.5.xsd">

  <bean id="employee1" class="com.earldouglas.directory.Employee">
    <property name="id" value="1" />
    <property name="name" value="Max Power" />
    <property name="title" value="The Leader" />
  </bean>

  <bean id="employee2" class="com.earldouglas.directory.Employee">
    <property name="id" value="2" />
    <property name="name" value="Number Two" />
    <property name="title" value="Second in Command" />
  </bean>

  <bean id="employee3" class="com.earldouglas.directory.Employee">
    <property name="id" value="3" />
    <property name="name" value="Jonny McDoe" />
    <property name="title" value="Work Man" />
  </bean>

  <util:map id="employees">
    <entry key="1" value-ref="employee1" />
    <entry key="2" value-ref="employee2" />
    <entry key="3" value-ref="employee3" />
  </util:map>

  <bean id="directoryEndpoint" class="com.earldouglas.springws.directory.DirectoryEndpoint">
    <property name="employees" ref="employees" />
  </bean>

  <oxm:jaxb2-marshaller id="marshaller"
    contextPath="com.earldouglas.directory" />

  <bean
    class="org.springframework.ws.server.endpoint.adapter.GenericMarshallingMethodEndpointAdapter">
    <constructor-arg ref="marshaller" />
  </bean>

  <bean
    class="org.springframework.ws.server.endpoint.mapping.PayloadRootAnnotationMethodEndpointMapping" />

</beans>
```

That's all there is to the server-side of this tutorial. For the client-side, methods of web service consumption abound. Eclipse's Web Service Client wizard was used with `directory.wsdl` to generate a series of verbose Axis-based classes, which are used by the following test to verify the function of the web service:

```java
public class DirectoryProxyTest extends TestCase {

  @Test
  public void testDirectoryProxy() throws Exception {
    DirectoryProxy directoryProxy = new DirectoryProxy();

    Id id = new Id();
    id.setId(BigInteger.valueOf(1));
    Employee employee = directoryProxy.employee(id);
    assertEquals(BigInteger.valueOf(1), employee.getId());
    assertEquals("Max Power", employee.getName());
    assertEquals("The Leader", employee.getTitle());
  }
}
```

