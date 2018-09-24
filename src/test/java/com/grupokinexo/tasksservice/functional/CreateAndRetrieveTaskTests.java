package com.grupokinexo.tasksservice.functional;

import com.grupokinexo.tasksservice.Main;
import com.grupokinexo.tasksservice.exceptions.ParserException;
import com.grupokinexo.tasksservice.helpers.BeanHelper;
import com.grupokinexo.tasksservice.models.requests.TaskRequest;
import com.grupokinexo.tasksservice.models.responses.TaskResponse;
import com.grupokinexo.tasksservice.parsers.JacksonJsonParser;
import com.grupokinexo.tasksservice.parsers.Parser;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;

import static com.nitorcreations.Matchers.reflectEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateAndRetrieveTaskTests {
    private static int PORT = 4567;
    private String baseUrl = "http://localhost:" + PORT + "/api";

    private Parser parser;

    @BeforeAll
    static void setupClass() {
        Main.main(null);

        Spark.awaitInitialization();
    }

    @AfterAll
    static void tearDown() {
        Spark.stop();
    }

    @BeforeEach
    void setup() {
        parser = BeanHelper.getBean("parser", JacksonJsonParser.class);
    }

    @Test
    void createShouldReturnCreatedResponse() throws IOException, ParserException {
        HttpResponse createdResponse = createTask();
        assertNotNull(createdResponse);
        assertEquals(HttpStatus.SC_CREATED, createdResponse.getStatusLine().getStatusCode());
        String content = EntityUtils.toString(createdResponse.getEntity());
        TaskResponse taskResponse = parser.parseToObject(content, TaskResponse.class);
        assertNotNull(taskResponse);

        HttpResponse getByIdResponse = getTaskById(taskResponse.getId());
        assertNotNull(getByIdResponse);
        assertEquals(HttpStatus.SC_OK, getByIdResponse.getStatusLine().getStatusCode());
        TaskResponse getByIdTaskResponse = parser.parseToObject(content, TaskResponse.class);

        assertThat(taskResponse, reflectEquals(getByIdTaskResponse));
    }

    @Test
    void getShouldReturnNotFoundWhenTaskDoesNotExists() {
        HttpResponse response = getTaskById(99);

        assertNotNull(response);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    }

    private HttpResponse createTask() throws IOException, ParserException {
        String url = baseUrl + "/tasks";
        HttpPost request = new HttpPost(url);

        TaskRequest requestTask = new TaskRequest();
        requestTask.setName("demo");
        requestTask.setMessage("demo message");

        request.setEntity(new StringEntity(parser.parseToString(requestTask)));
        return execute(request);
    }

    private HttpResponse getTaskById(int id) {
        String url = baseUrl + "/tasks/" + id;

        HttpGet request = new HttpGet(url);
        return execute(request);
    }

    private HttpResponse execute(HttpRequestBase request) {
        HttpClient client = HttpClientBuilder.create().build();

        request.addHeader("X-Caller-Id", "1");

        try {
            return client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}