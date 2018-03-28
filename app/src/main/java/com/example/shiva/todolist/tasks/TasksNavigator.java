package com.example.shiva.todolist.tasks;

import android.support.annotation.NonNull;

import com.example.shiva.todolist.addedittask.AddEditTaskActivity;
import com.example.shiva.todolist.taskdetail.TaskDetailActivity;
import com.example.shiva.todolist.util.providers.BaseNavigator;

/**
 * Defines the navigation actions that can be called from the task list screen.
 */
public class TasksNavigator {

    @NonNull
    private final BaseNavigator mNavigationProvider;

    public TasksNavigator(@NonNull BaseNavigator mNavigationProvider) {
        this.mNavigationProvider = mNavigationProvider;
    }

    /**
     * Start the activity that allows adding a new task.
     */
    void addNewTask() {
        mNavigationProvider.startActivityForResult(AddEditTaskActivity.class,
                AddEditTaskActivity.REQUEST_ADD_TASK);
    }

    /**
     * Open the details of a task.
     *
     * @param taskId id of the task.
     */
    void openTaskDetails(String taskId) {
        mNavigationProvider.startActivityForResultWithExtra(TaskDetailActivity.class, -1,
                TaskDetailActivity.EXTRA_TASK_ID, taskId);
    }

}
