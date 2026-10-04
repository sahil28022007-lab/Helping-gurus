package com.helpinggurus.dao;

import com.helpinggurus.exception.DataAccessException;
import java.util.*;

/** Generic DAO contract: T is the entity type, ID its key type (Generics + Interfaces). */
public interface Dao<T, ID> {
    Optional<T> findById(ID id) throws DataAccessException;
    List<T> findAll() throws DataAccessException;
    T save(T entity) throws DataAccessException;
    boolean delete(ID id) throws DataAccessException;
}
