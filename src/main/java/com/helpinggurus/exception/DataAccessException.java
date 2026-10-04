package com.helpinggurus.exception;

import java.sql.SQLException;

/** Wraps SQLException so upper layers do not depend on JDBC. */
public class DataAccessException extends HelpingGurusException {
    public DataAccessException(SQLException cause) { super("Database error: " + cause.getMessage(), cause); }
    public DataAccessException(String message) { super(message); }
}
