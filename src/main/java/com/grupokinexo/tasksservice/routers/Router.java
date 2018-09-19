package com.grupokinexo.tasksservice.routers;

import com.grupokinexo.tasksservice.controllers.TasksController;
import com.grupokinexo.tasksservice.exceptions.ExceptionHandler;
import com.grupokinexo.tasksservice.security.AuthorizationHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import spark.Spark;
import spark.servlet.SparkApplication;

public class Router implements SparkApplication {
    private static final String springConfigPath = "base-config.xml";

    private final TasksController tasksController;
    private final ExceptionHandler exceptionHandler;
    private final AuthorizationHandler authorizationHandler;

    public Router() {
        ApplicationContext context = new ClassPathXmlApplicationContext(springConfigPath);
        tasksController = context.getBean("tasksController", TasksController.class);
        exceptionHandler = context.getBean("exceptionHandler", ExceptionHandler.class);
        authorizationHandler = context.getBean("authorizationHandler", AuthorizationHandler.class);
    }

    public void init() {
        Spark.path("/api/tasks", () -> {
            Spark.get("", tasksController.getTask);
            Spark.get("/:id", tasksController.getById);
            Spark.post("", tasksController.createTask);
            Spark.put("/:id", tasksController.editTask);
        });

        Spark.exception(Exception.class, exceptionHandler::manageException);
        Spark.before(authorizationHandler::authorize);
    }

    public void destroy() {
    }
}