package com.earldouglas.guicespringjc.guice;

import com.earldouglas.guicespringjc.DefaultGreeter;
import com.earldouglas.guicespringjc.Greeter;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class XmlGuiceBootstrapper {

    public static void main(String[] arguments) {
        Injector injector = Guice.createInjector(new XmlGuiceModule());
        Greeter greeter = injector.getInstance(DefaultGreeter.class);
        System.out.println(greeter.getGreeting());
    }
}