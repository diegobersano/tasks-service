package com.grupokinexo.tasksservice.specifications;

import java.util.Objects;

public class RemoveByIdSpecification implements SqlSpecification {
    private int id;
    private Class tClass;

    public RemoveByIdSpecification(int id, Class tClass) {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(tClass, "tClass is required");
        this.id = id;
        this.tClass = tClass;
    }

    @Override
    public String toSqlQuery() {
        return String.format("delete from %s a where a.id = %d", tClass.getName(), id);
    }
}