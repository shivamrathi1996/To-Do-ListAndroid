package com.example.shiva.todolist.tasks;

import android.support.annotation.DrawableRes;

import com.example.shiva.todolist.data.Task;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * A task that should be displayed as an item in a list of tasks.
 * Contains the task, the action that should be triggered when taping on the task, the action that
 * should be triggered when checking or unchecking a task and the background that should be used for
 * this task.
 */
final class TaskItem {

    private Task mTask;

    @DrawableRes
    private int mBackground;

    private Action0 mOnClickAction;

    private Action1<Boolean> mOnCheckAction;

    public TaskItem(Task task, @DrawableRes int background,
                    Action0 onClickAction, Action1<Boolean> onCheckAction) {
        mTask = task;
        mBackground = background;
        mOnClickAction = onClickAction;
        mOnCheckAction = onCheckAction;
    }

    public Task getTask() {
        return mTask;
    }

    public int getBackground() {
        return mBackground;
    }

    /**
     * @return the action to be triggered on click events
     */
    public Action0 getOnClickAction() {
        return mOnClickAction;
    }

    /**
     * @return the action to be triggered when the task is checked (marked as done or active)
     */
    public Action1<Boolean> getOnCheckAction() {
        return mOnCheckAction;
    }
}
