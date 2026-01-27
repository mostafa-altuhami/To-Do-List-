package com.example.todolist.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.R;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<ToDoModel> todoList;


    public HistoryAdapter(List<ToDoModel> todoList) {
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        final ToDoModel item = todoList.get(position);
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setText(item.getTask());
        holder.task.setEnabled(false);
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }
    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder{

        CheckBox task;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.todoCheckBox);
        }

    }
}
