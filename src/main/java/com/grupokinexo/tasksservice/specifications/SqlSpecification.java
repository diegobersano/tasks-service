package com.grupokinexo.tasksservice.specifications;

public interface SqlSpecification extends Specification {
    String toSqlQuery();
}