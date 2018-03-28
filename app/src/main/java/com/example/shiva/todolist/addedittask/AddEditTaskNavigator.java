package com.example.shiva.todolist.addedittask;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.example.shiva.todolist.util.providers.BaseNavigator;

/**
 * Defines the navigation actions that can be called from AddEditTask screen
 */
public class AddEditTaskNavigator {

    @NonNull
    private final BaseNavigator mNavigationProvider;

    public AddEditTaskNavigator(@NonNull BaseNavigator navigationProvider) {
        mNavigationProvider = navigationProvider;
    }

    /**
     * When the task was saved, the activity should finish with success.
     */
    public void onTaskSaved() {
        mNavigationProvider.finishActivityWithResult(Activity.RESULT_OK);
    }
}
