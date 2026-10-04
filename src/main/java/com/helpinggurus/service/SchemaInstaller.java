package com.helpinggurus.service;

import com.helpinggurus.exception.DataAccessException;
import com.helpinggurus.util.ConnectionPool;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

/** Creates all tables from schema.sql on first start (JDBC Statement batch). */
public final class SchemaInstaller {
    private SchemaInstaller() {}
    /** Reads schema.sql and executes each statement. Safe to run on every start (CREATE TABLE IF NOT EXISTS). */
    public static void run() throws DataAccessException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection c = pool.borrow();
        try (InputStream in = SchemaInstaller.class.getResourceAsStream("/schema.sql");
             BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
             Statement st = c.createStatement()) {
            StringBuilder sb = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) if (!line.trim().startsWith("--")) sb.append(line).append('\n');
            for (String sql : sb.toString().split(";")) if (!sql.isBlank()) st.execute(sql.trim());
        } catch (IOException e) { throw new DataAccessException("Cannot read schema.sql"); }
        catch (SQLException e) { throw new DataAccessException(e); }
        finally { pool.release(c); }
    }
}
