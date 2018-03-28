package com.example.shiva.todolist.taskdetail;

import android.support.annotation.NonNull;

import com.example.shiva.todolist.addedittask.AddEditTaskActivity;
import com.example.shiva.todolist.addedittask.AddEditTaskFragment;
import com.example.shiva.todolist.util.providers.BaseNavigator;

/**
 * Defines the navigation actions that can be called from the Details screen.
 */
public class TaskDetailNavigator {

    @NonNull
    private final BaseNavigator mNavigationProvider;

    public TaskDetailNavigator(@NonNull BaseNavigator navigationProvider) {
        mNavigationProvider = navigationProvider;
    }

    /**
     * Finish the activity when task was deleted.
     */
    void onTaskDeleted() {
        mNavigationProvider.finishActivity();
    }

    /**
     * Open the AddEditTaskActivity to start editing the task.
     *
     * @param taskId the id of the task to be edited.
     */
    void onStartEditTask(String taskId) {
        mNavigationProvider.startActivityForResultWithExtra(AddEditTaskActivity.class,
                TaskDetailActivity.REQUEST_EDIT_TASK, AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID,
                taskId);
    }

    /**
     * Finish the activity when a task was added
     */
    void onTaskEdited() {
        mNavigationProvider.finishActivity();
    }
}
