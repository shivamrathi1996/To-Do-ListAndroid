package com.example.shiva.todolist.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.shiva.todolist.R;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Adapter for the list of tasks.
 */
final class TasksAdapter extends BaseAdapter {

    private List<TaskItem> mTasks;

    public TasksAdapter(List<TaskItem> tasks) {
        setList(tasks);
    }

    public void replaceData(List<TaskItem> tasks) {
        setList(tasks);
        notifyDataSetChanged();
    }

    private void setList(List<TaskItem> tasks) {
        mTasks = checkNotNull(tasks);
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public TaskItem getItem(int i) {
        return mTasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        TaskItemViewHolder viewHolder;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.task_item, viewGroup, false);

            viewHolder = new TaskItemViewHolder(rowView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (TaskItemViewHolder) rowView.getTag();
        }

        final TaskItem taskItem = getItem(i);
        viewHolder.bindItem(taskItem);

        return rowView;
    }
}
