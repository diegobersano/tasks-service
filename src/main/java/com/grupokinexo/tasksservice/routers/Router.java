package com.grupokinexo.tasksservice.routers;

import com.grupokinexo.tasksservice.controllers.TasksController;
import org.apache.http.HttpStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.servlet.SparkApplication;

public class Router implements SparkApplication {
    private static final String springConfigPath = "base-config.xml";

    private final TasksController tasksController;

    public Router() {
        ApplicationContext context = new ClassPathXmlApplicationContext(springConfigPath);
        tasksController = context.getBean("tasksController", TasksController.class);
    }

    private void manageException(Exception exception, Request request, Response response) {
        if (exception instanceof NullPointerException) {
            response.status(HttpStatus.SC_BAD_REQUEST);
        }
    }

    public void init() {
        Spark.path("/api/tasks", () -> {
            Spark.get("", tasksController.getTask);
            Spark.get("/:id", tasksController.getById);
            Spark.post("", tasksController.createTask);
            Spark.put("", tasksController.editTask);
        });

        Spark.exception(Exception.class, this::manageException);
    }

    public void destroy() {
    }
}
