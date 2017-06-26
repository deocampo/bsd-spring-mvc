package com.earldouglas.guicespringjc.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.earldouglas.guicespringjc.Greeter;

public class SimpleSpringBootstrapper {

    public static void main(String[] arguments) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SimpleSpringConfiguration.class);
        Greeter greeter = applicationContext.getBean(Greeter.class);
        System.out.println(greeter.getGreeting());
    }
}