package com.example.shiva.todolist.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.shiva.todolist.data.Task;
import com.example.shiva.todolist.util.schedulers.BaseSchedulerProvider;

import java.util.List;

import rx.Completable;
import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p/>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class TasksRepository implements TasksDataSource {

    @Nullable
    private static TasksRepository INSTANCE = null;

    @NonNull
    private final TasksDataSource mTasksRemoteDataSource;

    @NonNull
    private final TasksDataSource mTasksLocalDataSource;

    @NonNull
    private final BaseSchedulerProvider mBaseSchedulerProvider;

    // Prevent direct instantiation.
    private TasksRepository(@NonNull TasksDataSource tasksRemoteDataSource,
                            @NonNull TasksDataSource tasksLocalDataSource,
                            @NonNull BaseSchedulerProvider schedulerProvider) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
        mBaseSchedulerProvider = checkNotNull(schedulerProvider);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksRemoteDataSource the backend data source
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link TasksRepository} instance
     */
    public static TasksRepository getInstance(@NonNull TasksDataSource tasksRemoteDataSource,
                                              @NonNull TasksDataSource tasksLocalDataSource,
                                              @NonNull BaseSchedulerProvider schedulerProvider) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource,
                    schedulerProvider);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(TasksDataSource, TasksDataSource, BaseSchedulerProvider)}
     * to create a new instance next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets tasks from  local data source (SQLite).
     */
    @Override
    public Observable<List<Task>> getTasks() {
        return mTasksLocalDataSource.getTasks();
    }

    /**
     * Saves a task in the local and then in the remote repository
     *
     * @param task the task to be saved
     * @return a completable that emits when the task was saved or in case of error.
     */
    @NonNull
    @Override
    public Completable saveTask(@NonNull Task task) {
        checkNotNull(task);
        return mTasksLocalDataSource.saveTask(task)
                .andThen(mTasksRemoteDataSource.saveTask(task));
    }

    /**
     * Saves a list of tasks in the local and then in the remote repository
     *
     * @param tasks the tasks to be saved
     * @return a completable that emits when the tasks were saved or in case of error.
     */
    @NonNull
    @Override
    public Completable saveTasks(@NonNull List<Task> tasks) {
        checkNotNull(tasks);
        return mTasksLocalDataSource.saveTasks(tasks)
                .andThen(mTasksRemoteDataSource.saveTasks(tasks));
    }

    @Override
    public Completable completeTask(@NonNull Task task) {
        checkNotNull(task);
        return mTasksLocalDataSource.completeTask(task)
                .andThen(mTasksRemoteDataSource.completeTask(task));
    }

    @Override
    public Completable completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        return mTasksLocalDataSource.completeTask(taskId)
                .andThen(mTasksRemoteDataSource.completeTask(taskId));
    }

    @Override
    public Completable activateTask(@NonNull Task task) {
        checkNotNull(task);
        return mTasksLocalDataSource.activateTask(task)
                .andThen(mTasksRemoteDataSource.activateTask(task));
    }

    @Override
    public Completable activateTask(@NonNull String taskId) {
        checkNotNull(taskId);
        return mTasksLocalDataSource.activateTask(taskId)
                .andThen(mTasksRemoteDataSource.activateTask(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRemoteDataSource.clearCompletedTasks();
        mTasksLocalDataSource.clearCompletedTasks();
    }

    /**
     * Gets task from local data source (sqlite).
     */
    @Override
    public Observable<Task> getTask(@NonNull final String taskId) {
        checkNotNull(taskId);
        return mTasksLocalDataSource.getTask(taskId);
    }

    /**
     * Get the tasks from the remote data source and save them in the local data source.
     */
    @Override
    public Completable refreshTasks() {
        return mTasksRemoteDataSource.getTasks()
                .subscribeOn(mBaseSchedulerProvider.io())
                .doOnNext(mTasksLocalDataSource::saveTasks)
                .toCompletable();
    }

    /**
     * Delete tasks from remote and local repositories.
     */
    @Override
    public void deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks();
        mTasksLocalDataSource.deleteAllTasks();
    }

    /**
     * Delete a task based on the task id from remote and local repositories.
     *
     * @param taskId a task id
     */
    @Override
    public void deleteTask(@NonNull String taskId) {
        mTasksRemoteDataSource.deleteTask(checkNotNull(taskId));
        mTasksLocalDataSource.deleteTask(checkNotNull(taskId));
    }
}
