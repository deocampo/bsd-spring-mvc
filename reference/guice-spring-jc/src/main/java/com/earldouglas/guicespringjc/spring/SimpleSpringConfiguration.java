package com.earldouglas.guicespringjc.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.earldouglas.guicespringjc.DefaultGreeter;
import com.earldouglas.guicespringjc.Greeter;

@Configuration
public class SimpleSpringConfiguration {

    @Bean
    public Greeter greeter() {
        return new DefaultGreeter();
    }
}