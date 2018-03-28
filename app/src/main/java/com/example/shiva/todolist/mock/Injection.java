
package com.example.shiva.todolist.mock;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.shiva.todolist.data.source.TasksRepository;
import com.example.shiva.todolist.data.source.local.TasksLocalDataSource;
import com.example.shiva.todolist.mock.data.FakeTasksRemoteDataSource;
import com.example.shiva.todolist.util.providers.BaseNavigator;
import com.example.shiva.todolist.util.providers.BaseResourceProvider;
import com.example.shiva.todolist.util.providers.Navigator;
import com.example.shiva.todolist.util.providers.ResourceProvider;
import com.example.shiva.todolist.util.schedulers.BaseSchedulerProvider;
import com.example.shiva.todolist.util.schedulers.SchedulerProvider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for
 * {@link } at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    @NonNull
    public static TasksRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        return TasksRepository.getInstance(FakeTasksRemoteDataSource.getInstance(),
                TasksLocalDataSource.getInstance(context, provideSchedulerProvider()),
                provideSchedulerProvider());
    }

    @NonNull
    public static BaseSchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.getInstance();
    }

    @NonNull
    public static BaseResourceProvider createResourceProvider(@NonNull Context context) {
        return new ResourceProvider(context);
    }

    @NonNull
    public static BaseNavigator createNavigationProvider(@NonNull Activity activity) {
        return new Navigator(activity);
    }
}
