package com.grupokinexo.tasksservice.models.responses;

public class ErrorElement {
    public ErrorElement(String propertyName, String detail) {
        this.propertyName = propertyName;
        this.detail = detail;
    }

    private String propertyName;

    private String detail;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}