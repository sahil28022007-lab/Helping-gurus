package com.helpinggurus.model;

import com.helpinggurus.exception.VerificationException;

/** Interface: a user who controls authenticity of campaigns and photos. */
public interface Moderator {
    void verify(Campaign c) throws VerificationException;
    void reject(Campaign c);
    void moderate(Photo p, boolean approve);
}
