package com.earldouglas.guicespringjc.guice;

import com.earldouglas.guicespringjc.DefaultGreeter;
import com.earldouglas.guicespringjc.Greeter;
import com.google.inject.AbstractModule;

public class SimpleGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Greeter.class).to(DefaultGreeter.class);
    }
}
