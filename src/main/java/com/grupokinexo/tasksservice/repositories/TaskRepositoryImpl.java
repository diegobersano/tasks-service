package com.grupokinexo.tasksservice.repositories;

import com.grupokinexo.tasksservice.models.domain.Task;
import com.grupokinexo.tasksservice.specifications.FindByIdSpecification;
import com.grupokinexo.tasksservice.specifications.RemoveByIdSpecification;
import com.grupokinexo.tasksservice.specifications.SearchSpecification;
import com.grupokinexo.tasksservice.specifications.SqlSpecification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Transactional
@Repository
public class TaskRepositoryImpl implements TaskRepository {
    private final SessionFactory sessionFactory;

    public TaskRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public int add(Task object) {
        object.setCreatedOn(OffsetDateTime.now());
        Session session = sessionFactory.openSession();

        Transaction tx = session.beginTransaction();
        session.save(object);
        tx.commit();

        session.close();

        return object.getId();
    }

    @Override
    public void remove(Task object) {

    }

    @Override
    public void removeById(int id, Task task) {
        Session session = sessionFactory.openSession();

        SqlSpecification removeById = new RemoveByIdSpecification(id, task.getClass());
        Query<Task> query = session.createQuery(removeById.toSqlQuery(), Task.class);

        query.executeUpdate();

        session.close();
    }

    @Override
    public void update(Task object) {
        Session session = sessionFactory.openSession();
        Task taskToUpdate = getById(object.getId());
        taskToUpdate.setName(object.getName());
        taskToUpdate.setDescription(object.getDescription());

        session.update(object);
        session.close();
    }

    @Override
    public Task getById(int id) {
        Session session = sessionFactory.openSession();
        SqlSpecification getById = new FindByIdSpecification<>(id, Task.class);
        Query<Task> query = session.createQuery(getById.toSqlQuery(), Task.class);
        List<Task> tasks = query.getResultList();

        session.close();
        return tasks.isEmpty() ? null : tasks.get(0);
    }

    @Override
    public List<Task> search() {
        Session session = sessionFactory.openSession();
        SqlSpecification search = new SearchSpecification(Task.class);
        Query<Task> query = session.createQuery(search.toSqlQuery(), Task.class);

        session.close();
        return query.getResultList();
    }
}