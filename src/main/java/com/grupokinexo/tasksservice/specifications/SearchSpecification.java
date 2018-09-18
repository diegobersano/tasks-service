package com.grupokinexo.tasksservice.specifications;

import java.util.Objects;

public class SearchSpecification<T> implements SqlSpecification {
    private Class tClass;

    public SearchSpecification(Class tClass) {
        Objects.requireNonNull(tClass, "tClass is required");
        this.tClass = tClass;
    }

    @Override
    public String toSqlQuery() {
        return String.format("select a from %s a", tClass.getName());
    }
}