package com.grupokinexo.tasksservice.models.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "Tasks")
public class Task {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(name = "Name", length = 100)
    private String name;

    @NotNull
    @Column(name = "Description", length = 100)
    private String description;

    @NotNull
    @Column(name = "CreatedOn")
    private OffsetDateTime createdOn;

    @NotNull
    @Column(name = "CreatorId")
    private int creatorId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "TaskId")
    private Collection<UserTask> users = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public Collection<UserTask> getUsers() {
        return users;
    }

    public void setUsers(Collection<UserTask> users) {
        this.users = users;
    }
}