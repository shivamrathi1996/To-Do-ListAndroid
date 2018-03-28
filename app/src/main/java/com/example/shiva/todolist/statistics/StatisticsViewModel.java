package com.example.shiva.todolist.statistics;

import android.support.annotation.NonNull;

import com.example.shiva.todolist.R;
import com.example.shiva.todolist.data.Task;
import com.example.shiva.todolist.data.source.TasksRepository;
import com.example.shiva.todolist.util.EspressoIdlingResource;
import com.example.shiva.todolist.util.providers.BaseResourceProvider;

import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Retrieves the data and exposes updates for the progress of fetching the statistics.
 */
public class StatisticsViewModel {

    @NonNull
    private final TasksRepository mTasksRepository;

    @NonNull
    private final BaseResourceProvider mResourceProvider;

    public StatisticsViewModel(@NonNull TasksRepository tasksRepository,
                               @NonNull BaseResourceProvider resourceProvider) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
        mResourceProvider = checkNotNull(resourceProvider, "resourceProvider cannot be null");
    }

    /**
     * @return A stream of statistics to be displayed.
     */
    @NonNull
    public Observable<StatisticsUiModel> getUiModel() {
        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        Observable<Task> tasks = mTasksRepository
                .refreshTasks()
                .andThen(mTasksRepository.getTasks()
                        .first())
                .flatMap(Observable::from);

        Observable<Integer> completedTasks = tasks.filter(Task::isCompleted).count();
        Observable<Integer> activeTasks = tasks.filter(Task::isActive).count();

        return Observable.merge(
                Observable.just(mResourceProvider.getString(R.string.loading)),
                Observable.zip(completedTasks,
                        activeTasks,
                        (completed, active) -> getStatisticsString(active, completed))
                        .onErrorResumeNext(throwable -> {
                            return Observable.just(mResourceProvider.getString(R.string.loading_tasks_error));
                        }))
                .map(StatisticsUiModel::new);
    }

    @NonNull
    private String getStatisticsString(int numberOfActiveTasks, int numberOfCompletedTasks) {
        if (numberOfCompletedTasks == 0 && numberOfActiveTasks == 0) {
            return mResourceProvider.getString(R.string.statistics_no_tasks);
        } else {
            return mResourceProvider.getString(R.string.statistics_active_completed_tasks,
                    numberOfActiveTasks, numberOfCompletedTasks);
        }
    }
}
