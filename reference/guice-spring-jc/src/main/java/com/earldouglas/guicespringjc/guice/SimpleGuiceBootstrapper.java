package com.earldouglas.guicespringjc.guice;

import com.earldouglas.guicespringjc.Greeter;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class SimpleGuiceBootstrapper {

    public static void main(String[] arguments) {
        Injector injector = Guice.createInjector(new SimpleGuiceModule());
        Greeter greeter = injector.getInstance(Greeter.class);
        System.out.println(greeter.getGreeting());
    }
}