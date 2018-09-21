package com.grupokinexo.tasksservice.helpers;

import org.apache.http.HttpResponse;

import java.io.IOException;

public interface HttpHelper {
    String getBody(HttpResponse httpResponse) throws IOException;
}