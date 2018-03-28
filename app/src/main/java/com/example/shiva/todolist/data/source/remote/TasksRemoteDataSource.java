package com.example.shiva.todolist.data.source.remote;

import android.support.annotation.NonNull;

import com.example.shiva.todolist.data.Task;
import com.example.shiva.todolist.data.source.TasksDataSource;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Completable;
import rx.Observable;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class TasksRemoteDataSource implements TasksDataSource {

    private static TasksRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Task> TASKS_SERVICE_DATA;

    static {
        TASKS_SERVICE_DATA = new LinkedHashMap<>(2);
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.");
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!");
    }

    public static TasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TasksRemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private TasksRemoteDataSource() {
    }

    private static void addTask(String title, String description) {
        Task newTask = new Task(title, description);
        TASKS_SERVICE_DATA.put(newTask.getId(), newTask);
    }

    @Override
    public Observable<List<Task>> getTasks() {
        return Observable
                .from(TASKS_SERVICE_DATA.values())
                .delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .toList();
    }

    @Override
    public Observable<Task> getTask(@NonNull String taskId) {
        final Task task = TASKS_SERVICE_DATA.get(taskId);
        if (task != null) {
            return Observable.just(task).delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS);
        } else {
            return Observable.empty();
        }
    }

    @Override
    public Completable saveTask(@NonNull Task task) {
        return Completable.fromAction(() -> TASKS_SERVICE_DATA.put(task.getId(), task));
    }

    @Override
    public Completable saveTasks(@NonNull List<Task> tasks) {
        return Observable.from(tasks)
                .doOnNext(this::saveTask)
                .toCompletable();
    }

    @Override
    public Completable completeTask(@NonNull Task task) {
        return Completable.fromAction(() -> {
            Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
            TASKS_SERVICE_DATA.put(task.getId(), completedTask);
        });
    }

    @Override
    public Completable completeTask(@NonNull String taskId) {
        return Completable.fromAction(() -> {
            Task task = TASKS_SERVICE_DATA.get(taskId);
            task = new Task(task.getTitle(), task.getDescription(), taskId, true);
            TASKS_SERVICE_DATA.put(task.getId(), task);
        });
    }

    @Override
    public Completable activateTask(@NonNull Task task) {
        return Completable.fromAction(() -> {
            Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
            TASKS_SERVICE_DATA.put(task.getId(), activeTask);
        });
    }

    @Override
    public Completable activateTask(@NonNull String taskId) {
        return Completable.fromAction(() -> {
            Task task = TASKS_SERVICE_DATA.get(taskId);
            task = new Task(task.getTitle(), task.getDescription(), taskId, false);
            TASKS_SERVICE_DATA.put(task.getId(), task);
        });
    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = TASKS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public Completable refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
        return Completable.complete();
    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }
}
