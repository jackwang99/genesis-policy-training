/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.sso.server;

import com.eisgroup.genesis.boot.BootsrapBean;
import com.eisgroup.genesis.exception.InvocationError;
import com.eisgroup.genesis.sso.config.web.AuthorizationServerConfig;
import com.eisgroup.genesis.sso.config.web.SecurityConfig;
import com.eisgroup.genesis.sso.config.web.WebMvcConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.PreDestroy;
import javax.servlet.DispatcherType;
import java.util.EnumSet;

import static org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME;

/**
 * Bootstrap that loads the Jetty server and binds it to a specific port.
 * Initializes Spring's Web MVC and configures web application context.
 *
 * @author gvisokinskas
 */
public class JettyBootstrap implements BootsrapBean {
    private static final String ROOT_PATH = "/";
    private static final Class[] WEB_CONFIG_CLASSES = {
            SecurityConfig.class,
            WebMvcConfig.class,
            AuthorizationServerConfig.class
    };

    @Autowired
    private ApplicationContext rootContext;

    @Value("${genesis.http.server.port:8080}")
    private int serverPort;

    private Server server;
    private volatile boolean running;

    @Override
    public void start(ShutdownInvoker shutdownInvoker) {
        server = new Server(serverPort);
        try {
            server.setHandler(servletHandler());
            server.start();
            running = true;
        } catch (Exception e) {
            throw new InvocationError("Cannot initialize sso server", e);
        }
    }

    @PreDestroy
    public void stop() throws Exception {
        if (running && server.isStarted()) {
            running = false;
            server.setStopAtShutdown(true);
            server.stop();
        }
    }

    private ServletContextHandler servletHandler() {
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath(ROOT_PATH);
        contextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());

        WebApplicationContext webAppContext = createWebAppContext();
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webAppContext);
        ServletHolder springServletHolder = new ServletHolder(dispatcherServlet);

        contextHandler.addServlet(springServletHolder, ROOT_PATH);
        contextHandler.addEventListener(new ContextLoaderListener(webAppContext));

        contextHandler.addFilter(new FilterHolder(new DelegatingFilterProxy(DEFAULT_FILTER_NAME, webAppContext)),
                "/*", EnumSet.allOf(DispatcherType.class));

        return contextHandler;
    }

    private WebApplicationContext createWebAppContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setParent(rootContext);
        context.register(WEB_CONFIG_CLASSES);
        return context;
    }
}