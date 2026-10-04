package com.helpinggurus.model;

/** Abstract base class for all accounts. Subclasses give role-specific behaviour (polymorphism). */
public abstract class User {
    private int id;
    private final String name, email, passwordHash;

    protected User(int id, String name, String email, String passwordHash) {
        this.id = id; this.name = name; this.email = email; this.passwordHash = passwordHash;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }

    /** Role name stored in the database: ADMIN, CREATOR or CONTRIBUTOR. */
    public abstract String getRole();
    /** Where this kind of user lands after login. */
    public String getHomePath() { return "/"; }

    /** Factory: builds the right subclass from the role stored in the database. */
    public static User of(String role, int id, String name, String email, String hash) {
        return switch (role) {
            case "ADMIN" -> new Admin(id, name, email, hash);
            case "CREATOR" -> new Creator(id, name, email, hash);
            default -> new Contributor(id, name, email, hash);
        };
    }
    @Override public String toString() { return getRole() + ":" + name; }
}
