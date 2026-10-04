package com.helpinggurus.model;

/** A campaign organizer. Inherits from Contributor, so creators can also donate. */
public class Creator extends Contributor {
    public Creator(int id, String name, String email, String hash) { super(id, name, email, hash); }
    @Override public String getRole() { return "CREATOR"; }
    @Override public String getHomePath() { return "/creator/dashboard"; }
}
