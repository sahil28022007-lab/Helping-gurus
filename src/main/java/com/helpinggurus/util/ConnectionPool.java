package com.helpinggurus.util;

import com.helpinggurus.exception.DataAccessException;
import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Simple JDBC connection pool. A BlockingQueue hands connections to request threads safely
 * (collections + multithreading). Settings come from db.properties; -Ddb.url etc. override them.
 */
public final class ConnectionPool {
    private static ConnectionPool instance;
    private final BlockingQueue<Connection> idle;
    private final Properties props = new Properties();

    // Loads db.properties, lets -D system properties override it, then opens all connections up front.
    private ConnectionPool() throws DataAccessException {
        try (InputStream in = ConnectionPool.class.getResourceAsStream("/db.properties")) {
            if (in != null) props.load(in);
            for (String k : new String[]{"db.driver", "db.url", "db.user", "db.password", "db.poolSize", "upload.dir"})
                if (System.getProperty(k) != null) props.setProperty(k, System.getProperty(k));
            Class.forName(props.getProperty("db.driver"));
            int size = Integer.parseInt(props.getProperty("db.poolSize", "5"));
            idle = new ArrayBlockingQueue<>(size);
            for (int i = 0; i < size; i++) idle.add(open());
        } catch (IOException | ClassNotFoundException e) {
            throw new DataAccessException("Cannot initialise database: " + e.getMessage());
        } catch (SQLException e) { throw new DataAccessException(e); }
    }

    /** Singleton access; the pool is created lazily on first use. */
    public static synchronized ConnectionPool getInstance() throws DataAccessException {
        if (instance == null) instance = new ConnectionPool();
        return instance;
    }

    // Opens one new physical JDBC connection from the configured URL, user and password.
    private Connection open() throws SQLException {
        return DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.user"), props.getProperty("db.password"));
    }

    /** Reads a setting such as upload.dir; the ${user.home} placeholder is expanded. */
    public String getProperty(String key) {
        return props.getProperty(key, "").replace("${user.home}", System.getProperty("user.home"));
    }

    /** Blocks up to 5 seconds waiting for a free connection. */
    public Connection borrow() throws DataAccessException {
        try {
            Connection c = idle.poll(5, TimeUnit.SECONDS);
            if (c == null) throw new DataAccessException("No database connection available, try again");
            if (!c.isValid(2)) c = open();
            return c;
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new DataAccessException("Interrupted"); }
        catch (SQLException e) { throw new DataAccessException(e); }
    }

    /** Returns a connection to the pool. Always call this in a finally block. */
    public void release(Connection c) { if (c != null) idle.offer(c); }

    /** Closes every pooled connection when the application stops. */
    public static synchronized void shutdown() {
        if (instance == null) return;
        for (Connection c : instance.idle) try { c.close(); } catch (SQLException ignored) {}
        instance = null;
    }
}
