package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    Optional<T> getLast();
    void save(T t);
    void delete(T t);

}
