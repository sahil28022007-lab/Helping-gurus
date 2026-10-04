package com.helpinggurus.model;

/** Interface: something whose authenticity can be scored. */
public interface Verifiable {
    boolean isVerified();
    int getTrustScore();
}
