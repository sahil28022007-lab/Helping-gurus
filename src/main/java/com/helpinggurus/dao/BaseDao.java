package com.helpinggurus.dao;

import com.helpinggurus.exception.DataAccessException;
import com.helpinggurus.util.ConnectionPool;
import java.sql.*;
import java.util.*;

/** All JDBC plumbing lives here: pooled connections, PreparedStatements, transactions, row mapping. */
public abstract class BaseDao {
    /** A function that may throw SQLException (lets lambdas use JDBC directly). */
    @FunctionalInterface public interface SqlFunction<T, R> { R apply(T t) throws SQLException; }
    /** Converts the current ResultSet row into an object of type T. */
    @FunctionalInterface public interface RowMapper<T> { T map(ResultSet rs) throws SQLException; }

    /**
     * Borrows a pooled connection, runs the work, and always returns the connection.
     * SQLException is translated to {@link DataAccessException} so upper layers stay JDBC-free.
     */
    protected <R> R withConn(SqlFunction<Connection, R> fn) throws DataAccessException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection c = pool.borrow();
        try { return fn.apply(c); }
        catch (SQLException e) { throw new DataAccessException(e); }
        finally { pool.release(c); }
    }

    /** Runs several statements atomically: commit on success, rollback on any failure. */
    protected <R> R tx(SqlFunction<Connection, R> fn) throws DataAccessException {
        return withConn(c -> {
            boolean old = c.getAutoCommit();
            c.setAutoCommit(false);
            try { R r = fn.apply(c); c.commit(); return r; }
            catch (SQLException | RuntimeException e) { c.rollback(); throw e; }
            finally { c.setAutoCommit(old); }
        });
    }

    /** Binds positional ? parameters of a PreparedStatement (prevents SQL injection). */
    protected static void bind(PreparedStatement ps, Object... a) throws SQLException {
        for (int i = 0; i < a.length; i++) ps.setObject(i + 1, a[i]);
    }

    /** Runs a SELECT and maps every row with the given {@link RowMapper}. */
    protected <T> List<T> query(String sql, RowMapper<T> mapper, Object... args) throws DataAccessException {
        return withConn(c -> {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                bind(ps, args);
                try (ResultSet rs = ps.executeQuery()) {
                    List<T> out = new ArrayList<>();
                    while (rs.next()) out.add(mapper.map(rs));
                    return out;
                }
            }
        });
    }

    /** Runs a SELECT and returns the first row, or empty when nothing matches. */
    protected <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... args) throws DataAccessException {
        return query(sql, mapper, args).stream().findFirst();
    }

    /** Runs an INSERT, UPDATE or DELETE and returns the number of affected rows. */
    protected int update(String sql, Object... args) throws DataAccessException {
        return withConn(c -> updateOn(c, sql, args));
    }

    /** Runs an INSERT on its own connection and returns the generated key. */
    protected int insert(String sql, Object... args) throws DataAccessException {
        return withConn(c -> insertOn(c, sql, args));
    }

    /** Same as update, but on a connection supplied by the caller (used inside transactions). */
    protected static int updateOn(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) { bind(ps, args); return ps.executeUpdate(); }
    }

    /** Executes an INSERT and returns the generated primary key. */
    protected static int insertOn(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, args);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { return k.next() ? k.getInt(1) : 0; }
        }
    }
}
