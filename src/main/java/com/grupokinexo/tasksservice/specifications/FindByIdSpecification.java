package com.grupokinexo.tasksservice.specifications;

import java.util.Objects;

public class FindByIdSpecification<T> implements SqlSpecification {
    private T id;
    private Class tClass;

    public FindByIdSpecification(T id, Class tClass) {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(tClass, "tClass is required");
        this.id = id;
        this.tClass = tClass;
    }

    @Override
    public String toSqlQuery() {
        return String.format("select a from %s a where a.id=%s", tClass.getName(), id.toString());
    }
}