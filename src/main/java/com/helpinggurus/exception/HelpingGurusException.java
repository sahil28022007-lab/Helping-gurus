package com.helpinggurus.exception;

/** Base checked exception for all application errors (shown to the user as a friendly message). */
public class HelpingGurusException extends Exception {
    public HelpingGurusException(String message) { super(message); }
    public HelpingGurusException(String message, Throwable cause) { super(message, cause); }
}
