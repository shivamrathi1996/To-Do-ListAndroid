
package com.example.shiva.todolist.taskdetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.shiva.todolist.R;
import com.google.common.base.Preconditions;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Main UI for the task detail screen.
 */
public class TaskDetailFragment extends Fragment {

    private static final String TAG = TaskDetailFragment.class.getSimpleName();

    @NonNull
    private static final String ARGUMENT_TASK_ID = "TASK_ID";

    private TextView mLoadingProgress;

    private TextView mDetailTitle;

    private TextView mDetailDescription;

    private CheckBox mDetailCompleteStatus;

    @Nullable
    private TaskDetailViewModel mViewModel;

    @Nullable
    private CompositeSubscription mSubscription;

    public static TaskDetailFragment newInstance(@Nullable String taskId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_TASK_ID, taskId);
        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.taskdetail_frag, container, false);
        setHasOptionsMenu(true);
        mLoadingProgress = root.findViewById(R.id.loading_progress);
        mDetailTitle = root.findViewById(R.id.task_detail_title);
        mDetailDescription = root.findViewById(R.id.task_detail_description);
        mDetailCompleteStatus = root.findViewById(R.id.task_detail_complete);

        setupFab();

        mViewModel = TaskDetailModule.createTaskDetailsViewModel(getTaskId(), getActivity());

        return root;
    }

    @Override
    public void onResume() {
        bindViewModel();
        super.onResume();
    }

    @Override
    public void onPause() {
        unbindViewModel();
        super.onPause();
    }

    private void setupFab() {
        FloatingActionButton fab =
                getActivity().findViewById(R.id.fab_edit_task);

        fab.setOnClickListener(__ -> editTask());
    }

    private void bindViewModel() {
        // using a CompositeSubscription to gather all the subscriptions, so all of them can be
        // later unsubscribed together
        mSubscription = new CompositeSubscription();

        // subscribe to the emissions of the Ui Model
        // every time a new Ui Model, update the View
        mSubscription.add(getViewModel().getTaskUiModel()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        this::updateView,
                        // onError
                        __ -> showMissingTask()));

        // The ViewModel holds an observable containing the state of the UI.
        // subscribe to the emissions of the loading indicator visibility
        // for every emission, update the visibility of the loading indicator
        mSubscription.add(getViewModel().getLoadingIndicatorVisibility()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        this::setLoadingIndicatorVisibility,
                        // onError
                        __ -> showMissingTask()));

        // subscribe to the emissions of the snackbar text
        // every time the snackbar text emits, show the snackbar
        mSubscription.add(getViewModel().getSnackbarText()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        this::showSnackbar,
                        // onError
                        throwable -> Log.e(TAG, "Unable to display snackbar text", throwable)));
    }

    private void unbindViewModel() {
        // unsubscribing from all the subscriptions to ensure we don't have any memory leaks
        getSubscription().unsubscribe();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                deleteTask();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateView(TaskUiModel model) {
        int titleVisibility = model.isShowTitle() ? View.VISIBLE : View.GONE;
        int descriptionVisibility = model.isShowDescription() ? View.VISIBLE : View.GONE;

        mDetailTitle.setVisibility(titleVisibility);
        mDetailTitle.setText(model.getTitle());
        mDetailDescription.setVisibility(descriptionVisibility);
        mDetailDescription.setText(model.getDescription());
        showCompletionStatus(model.isChecked());
    }

    private void setLoadingIndicatorVisibility(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        mLoadingProgress.setVisibility(visibility);
    }

    private void showCompletionStatus(final boolean complete) {
        mDetailCompleteStatus.setChecked(complete);
        mDetailCompleteStatus.setOnCheckedChangeListener(
                (buttonView, isChecked) -> taskCheckChanged(isChecked));
    }

    private void taskCheckChanged(final boolean checked) {
        getSubscription().add(getViewModel().taskCheckChanged(checked)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        () -> {
                            // nothing to do here
                        },
                        // onError
                        throwable -> showMissingTask()));
    }

    private void deleteTask() {
        getSubscription().add(getViewModel().deleteTask()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        () -> {
                            //nothing to do here
                        },
                        // onError
                        __ -> showMissingTask()));
    }

    private void editTask() {
        getSubscription().add(getViewModel().editTask()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(  // onNext
                        () -> {
                            //nothing to do here
                        },
                        // onError
                        __ -> showMissingTask()));
    }

    private void showSnackbar(@StringRes int text) {
        Snackbar.make(getView(), text, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mViewModel.handleActivityResult(requestCode, resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showMissingTask() {
        mDetailTitle.setText("");
        mDetailDescription.setText(getString(R.string.no_data));
    }

    @NonNull
    private TaskDetailViewModel getViewModel() {
        return Preconditions.checkNotNull(mViewModel);
    }

    @NonNull
    private CompositeSubscription getSubscription() {
        return Preconditions.checkNotNull(mSubscription);
    }

    @Nullable
    private String getTaskId() {
        if (getArguments() != null) {
            return getArguments().getString(ARGUMENT_TASK_ID);
        }
        return null;
    }
}
