package com.earldouglas.guicespringjc.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.earldouglas.guicespringjc.Greeter;

public class XmlSpringBootstrapper {

    public static void main(String[] arguments) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(XmlSpringConfiguration.class);
        Greeter greeter = applicationContext.getBean(Greeter.class);
        System.out.println(greeter.getGreeting());
    }
}