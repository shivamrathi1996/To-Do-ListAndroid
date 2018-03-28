package com.example.shiva.todolist.taskdetail;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.shiva.todolist.mock.Injection;
import com.example.shiva.todolist.util.providers.BaseNavigator;

/**
 * Enables inversion of control of the ViewModel and Navigator classes for task detail.
 */
class TaskDetailModule {

    @NonNull
    public static TaskDetailViewModel createTaskDetailsViewModel(
            @Nullable String taskId,
            @NonNull Activity activity) {
        Context appContext = activity.getApplicationContext();
        BaseNavigator navigationProvider = Injection.createNavigationProvider(activity);
        return new TaskDetailViewModel(taskId, Injection.provideTasksRepository(appContext),
                createTaskDetailNavigator(navigationProvider));
    }

    @NonNull
    public static TaskDetailNavigator createTaskDetailNavigator(
            @NonNull BaseNavigator navigationProvider) {
        return new TaskDetailNavigator(navigationProvider);
    }

}
