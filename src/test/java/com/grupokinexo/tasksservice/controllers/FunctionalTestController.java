package com.grupokinexo.tasksservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupokinexo.tasksservice.Main;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.models.requests.TaskRequest;
import com.grupokinexo.tasksservice.models.responses.TaskResponse;
import com.grupokinexo.tasksservice.parsers.JacksonJsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FunctionalTestController {
    private static int PORT = 4567;

    @BeforeClass
    public static void setup() {
        Main.main(null);

        Spark.awaitInitialization();
    }

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @Test
    public void createShouldReturnCreatedResponse() throws IOException, ParserException {
        int createdId = createTask();
        getTaskById(createdId);
    }

    @Test
    public void getShouldReturnNotFoundWhenTaskDoesNotExists() throws IOException {
        String url = "http://localhost:" + PORT + "/api/tasks/" + 99;

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    }

    private int createTask() throws IOException, ParserException {
        String url = "http://localhost:" + PORT + "/api/tasks";
        JacksonJsonParser parser = new JacksonJsonParser(new ObjectMapper());

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);

        TaskRequest requestTask = new TaskRequest();
        requestTask.setName("demo");
        requestTask.setMessage("demo message");

        request.setEntity(new StringEntity(parser.parseToString(requestTask)));
        HttpResponse response = client.execute(request);

        assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
        String content = EntityUtils.toString(response.getEntity());

        TaskResponse taskResponse = parser.parseToObject(content, TaskResponse.class);

        assertNotNull(taskResponse);
        return taskResponse.getId();
    }

    private void getTaskById(int id) throws IOException {
        String url = "http://localhost:" + PORT + "/api/tasks/" + id;

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }
}

