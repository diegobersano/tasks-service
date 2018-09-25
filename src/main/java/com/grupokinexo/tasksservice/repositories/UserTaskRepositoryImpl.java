package com.grupokinexo.tasksservice.repositories;

import com.grupokinexo.tasksservice.models.domain.Task;
import com.grupokinexo.tasksservice.models.domain.UserTask;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Collection;

public class UserTaskRepositoryImpl implements UserTaskRepository {
    private final SessionFactory sessionFactory;
    private final TaskRepository taskRepository;

    public UserTaskRepositoryImpl(SessionFactory sessionFactory, TaskRepository taskRepository) {
        this.sessionFactory = sessionFactory;
        this.taskRepository = taskRepository;
    }

    @Override
    public Collection<UserTask> getByTaskId(int taskId) {
        Task task = taskRepository.getById(taskId);

        if (task == null) {
            return new ArrayList<>();
        }

        return task.getUsers();
    }

    @Override
    public void addToTask(int taskId, Collection<Integer> users) {
        Task task = taskRepository.getById(taskId);

        Collection<UserTask> toRemove = new ArrayList<>();
        Collection<UserTask> toAdd = new ArrayList<>();

        task.getUsers().forEach(user -> {
            boolean exists = users.parallelStream().anyMatch(x -> x == user.getId());

            if (!exists) {
                toRemove.add(user);
            }
        });

        users.forEach(user -> {
            boolean exists = task.getUsers().parallelStream().anyMatch(x -> x.getId() == user);

            if (!exists) {
                UserTask userToAdd = new UserTask();
                userToAdd.setUserId(user);
                userToAdd.setTask(task);
                toAdd.add(userToAdd);
            }
        });

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        for (UserTask userTask : toRemove) {
            session.remove(userTask);
        }

        for (UserTask userTask : toAdd) {
            session.persist(userTask);
        }

        transaction.commit();
        session.close();
    }
}