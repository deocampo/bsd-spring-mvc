package com.earldouglas.guicespringjc.guice;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.inject.AbstractModule;
import com.google.inject.spring.SpringIntegration;

public class XmlGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("com/earldouglas/guicespringjc/spring/config.xml");
        SpringIntegration.bindAll(binder(), applicationContext);
    }
}