package com.helpinggurus.exception;

/** Thrown when an admin tries to publish a campaign whose four checks have not all passed. */
public class VerificationException extends HelpingGurusException {
    public VerificationException(String message) { super(message); }
}
