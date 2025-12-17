package com.example.todolist.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.todolist.Model.ToDoModel;
import com.example.todolist.R;

import java.util.List;

public class TasksInfoAdapter extends RecyclerView.Adapter<TasksInfoAdapter.TasksViewHolder> {

    private List<ToDoModel> todoList;

    public TasksInfoAdapter(List<ToDoModel> todoList) {
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public TasksInfoAdapter.TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.taskinfo_layout, parent, false);
        return new TasksViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksInfoAdapter.TasksViewHolder holder, int position) {
        final ToDoModel item = todoList.get(position);
        holder.tv_task.setText(item.getTask());
        holder.tv_count.setText(String.valueOf(item.getExecutionCounter()));
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public static class TasksViewHolder extends RecyclerView.ViewHolder {

        TextView tv_task, tv_count;

        View v;
        public TasksViewHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView;
            tv_task = itemView.findViewById(R.id.taskName_tv);
            tv_count = itemView.findViewById(R.id.taskCount_tv);
        }
    }
}
