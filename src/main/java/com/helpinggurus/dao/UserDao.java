package com.helpinggurus.dao;

import com.helpinggurus.exception.DataAccessException;
import com.helpinggurus.model.User;
import java.util.*;

/** Database access for user accounts (users table). */
public class UserDao extends BaseDao implements Dao<User, Integer> {
    // The factory method User.of() builds the right subclass from the stored role (polymorphism).
    private static final RowMapper<User> MAPPER = rs ->
        User.of(rs.getString("role"), rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("password_hash"));

    @Override public Optional<User> findById(Integer id) throws DataAccessException {
        return queryOne("SELECT * FROM users WHERE id=?", MAPPER, id);
    }
    /** Looks up an account by email (stored in lower case). */
    public Optional<User> findByEmail(String email) throws DataAccessException {
        return queryOne("SELECT * FROM users WHERE email=?", MAPPER, email.toLowerCase());
    }
    @Override public List<User> findAll() throws DataAccessException {
        return query("SELECT * FROM users ORDER BY id", MAPPER);
    }
    public List<User> findByRole(String role) throws DataAccessException {
        return query("SELECT * FROM users WHERE role=? ORDER BY name", MAPPER, role);
    }
    /** Inserts a new account and sets its generated id. Only the password hash is stored. */
    @Override public User save(User u) throws DataAccessException {
        u.setId(insert("INSERT INTO users(name,email,password_hash,role) VALUES(?,?,?,?)",
                u.getName(), u.getEmail().toLowerCase(), u.getPasswordHash(), u.getRole()));
        return u;
    }
    @Override public boolean delete(Integer id) throws DataAccessException {
        return update("DELETE FROM users WHERE id=?", id) > 0;
    }
    public int count() throws DataAccessException {
        return query("SELECT COUNT(*) AS n FROM users", rs -> rs.getInt("n")).get(0);
    }
}
