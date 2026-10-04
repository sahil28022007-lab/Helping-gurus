package com.helpinggurus.exception;

/** Thrown when a donation amount breaks the minimum or maximum rule. */
public class InvalidDonationException extends HelpingGurusException {
    public InvalidDonationException(String message) { super(message); }
}
