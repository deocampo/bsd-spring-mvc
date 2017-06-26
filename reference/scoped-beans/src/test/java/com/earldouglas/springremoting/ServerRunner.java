package com.earldouglas.springremoting;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerRunner {

    private static Server server;

    public static void main(String[] args) throws Exception {

        server = new Server();

        Connector connector = new SelectChannelConnector();
        connector.setPort(8080);

        server.addConnector(connector);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/scopedbeans");

        webAppContext.setWar("src/main/webapp");

        server.setHandler(webAppContext);

        server.start();
        server.join();

    }
}
