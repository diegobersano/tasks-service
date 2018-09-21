package com.grupokinexo.tasksservice.helpers;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpHelperImpl implements HttpHelper {

    @Override
    public String getBody(HttpResponse httpResponse) throws IOException {
        return EntityUtils.toString(httpResponse.getEntity());
    }
}