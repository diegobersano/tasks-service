package com.grupokinexo.tasksservice.models.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ShareTaskRequest {
    @NotNull
    @NotEmpty
    private int[] userIds;

    public int[] getUserIds() {
        return userIds;
    }

    public void setUserIds(int[] userIds) {
        this.userIds = userIds;
    }
}