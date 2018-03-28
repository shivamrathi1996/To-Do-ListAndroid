package com.example.android.architecture.blueprints.todoapp.taskdetail;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.architecture.blueprints.todoapp.Injection;
import com.example.android.architecture.blueprints.todoapp.util.providers.BaseNavigator;

/**
 * Enables inversion of control of the ViewModel and Navigator classes for task detail.
 */
class TaskDetailModule {

    @NonNull
    public static com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailViewModel createTaskDetailsViewModel(
            @Nullable String taskId,
            @NonNull Activity activity) {
        Context appContext = activity.getApplicationContext();
        BaseNavigator navigationProvider = Injection.createNavigationProvider(activity);
        return new com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailViewModel(taskId, Injection.provideTasksRepository(appContext),
                createTaskDetailNavigator(navigationProvider));
    }

    @NonNull
    public static com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailNavigator createTaskDetailNavigator(
            @NonNull BaseNavigator navigationProvider) {
        return new com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailNavigator(navigationProvider);
    }

}
