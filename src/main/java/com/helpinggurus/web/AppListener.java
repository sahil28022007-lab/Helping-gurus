package com.helpinggurus.web;

import com.helpinggurus.service.*;
import com.helpinggurus.util.ConnectionPool;
import javax.servlet.*;
import javax.servlet.annotation.WebListener;

/** Runs once at startup/shutdown: creates tables, loads demo data, starts the background threads. */
@WebListener
public class AppListener implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent e) {
        try {
            SchemaInstaller.run();
            SeedData.runIfEmpty();
            NotificationService.start();
            StatsService.start();
            e.getServletContext().log("Helping Gurus started");
        } catch (Exception ex) { throw new IllegalStateException("Startup failed: " + ex.getMessage(), ex); }
    }
    @Override public void contextDestroyed(ServletContextEvent e) {
        NotificationService.stop();
        StatsService.stop();
        ConnectionPool.shutdown();
    }
}
