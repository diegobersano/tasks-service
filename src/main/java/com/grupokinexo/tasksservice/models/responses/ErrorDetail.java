package com.grupokinexo.tasksservice.models.responses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ErrorDetail {
    public ErrorDetail() {
        elements = new ArrayList<>();
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
        this.elements.addAll(errors);
    }
}