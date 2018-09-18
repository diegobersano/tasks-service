package com.grupokinexo.tasksservice.repositories;

import java.util.List;

public interface BaseRepository<T> {
    int add(T object);

    void remove(T object);

    void removeById(int id, T tClass);

    void update(T object);

    T getById(int id);

    List<T> search();
}