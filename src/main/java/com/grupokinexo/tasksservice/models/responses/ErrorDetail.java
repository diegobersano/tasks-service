package com.grupokinexo.tasksservice.models.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorDetail {
    public ErrorDetail() {
        elements = new ArrayList<>();
    }

    public ErrorDetail(String message) {
        this();
        setMessage(message);
    }

    private String message;

    private List<ErrorElement> elements;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ErrorElement> getElements() {
        return elements;
    }

    public void addElements(ErrorElement element) {
        this.elements.add(element);
    }

    public void addElements(Collection<ErrorElement> errors) {
        if (errors != null && !errors.isEmpty()) {
            this.elements.addAll(errors);
        }
    }
}