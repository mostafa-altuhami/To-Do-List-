package com.example.todolist.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.AddNewTask;
import com.example.todolist.MainActivity;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.R;
import com.example.todolist.Utils.DatabaseHandler;
import com.google.android.material.imageview.ShapeableImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClicked(ToDoModel item, int position);
    }

    private List<ToDoModel> todoList;
    private DatabaseHandler db;
    private WeakReference<MainActivity> activityRef;
    private Context context;
    private ExecutorService executor;
    private ConcurrentHashMap<Integer, Boolean> flagStates = new ConcurrentHashMap<>();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public ToDoAdapter() {}

    public ToDoAdapter(DatabaseHandler db, MainActivity activity, List<ToDoModel> todoList) {
        this.db = db;
        this.activityRef = new WeakReference<>(activity);
        this.context = activity;
        this.todoList = todoList;
        this.executor = Executors.newSingleThreadExecutor();
        db.openDatabase();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (todoList == null || position >= todoList.size()) return;

        final ToDoModel item = todoList.get(position);
        if (item == null) return;

        // Clear previous listener to avoid issues
        holder.task.setOnCheckedChangeListener(null);

        // Set checkbox state
        holder.task.setChecked(item.getStatus() == 1);
        holder.task.setText(item.getTask());

        // Set checkbox change listener
        holder.task.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleCheckboxChange(holder, item, isChecked);
        });

        // Bind the item with this adapter instance
        holder.bind(item, position, this);

        boolean currentFlag = getFlagState(item.getId());
        int executionCount = item.getExecutionCounter();
        handleItemClick(holder.counter, holder.imageView, executionCount, currentFlag);
    }

    // Helper method to handle checkbox changes
    private void handleCheckboxChange(ViewHolder holder, ToDoModel item, boolean isChecked) {
        int newStatus = isChecked ? 1 : 0;
        int oldStatus = item.getStatus();

        if (newStatus != oldStatus) {
            // Update model immediately
            item.setStatus(newStatus);

            // Update database in background
            executor.execute(() -> {
                try {

                    if (isChecked) {
                        db.incrementCounter(item.getId());
                        db.updateStatus(item.getId(), 1);
                    } else {
                        db.decrementCounter(item.getId());
                        db.updateStatus(item.getId(), 0);
                    }
                    int counterValue = db.getCounter(item.getId());
                    mainHandler.post(() -> holder.counter.setText(String.valueOf(counterValue)));
                } catch (Exception e) {
                    // Revert on error
                    mainHandler.post(() -> {
                        item.setStatus(oldStatus);
                        holder.task.setOnCheckedChangeListener(null);
                        holder.task.setChecked(oldStatus == 1);
                        // Re-set the listener
                        holder.task.setOnCheckedChangeListener((buttonView, isChecked2) -> {
                            handleCheckboxChange(holder, item, isChecked2);
                        });
                    });
                }
            });
        }
    }

    private void setupAchievement(TextView counterView, ShapeableImageView achievementIcon,
                                  int backgroundRes, int iconRes, int colorRes) {
        counterView.setBackgroundResource(backgroundRes);
        achievementIcon.setVisibility(View.VISIBLE);
        achievementIcon.setImageResource(iconRes);
        achievementIcon.setColorFilter(ContextCompat.getColor(context, colorRes));
    }

    @Override
    public int getItemCount() {
        return todoList != null ? todoList.size() : 0;
    }

    public Context getContext() {
        return context;
    }

    public void setTasks(List<ToDoModel> newTodoList) {
        if (newTodoList == null) {

        }

        if (todoList == null) {
            todoList = new ArrayList<>();
        }

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return todoList.size();
            }

            @Override
            public int getNewListSize() {
                return newTodoList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return todoList.get(oldItemPosition).getId() == newTodoList.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                ToDoModel oldItem = todoList.get(oldItemPosition);
                ToDoModel newItem = newTodoList.get(newItemPosition);
                return oldItem.getTask().equals(newItem.getTask()) &&
                        oldItem.getStatus() == newItem.getStatus();
            }
        });

        this.todoList = newTodoList;
        diffResult.dispatchUpdatesTo(this);
    }

    public void deleteItem(int position) {
        if (todoList == null || position < 0 || position >= todoList.size()) return;

        ToDoModel item = todoList.get(position);

        // Manual backup creation since no constructor available
        ToDoModel itemBackup = new ToDoModel();
        itemBackup.setId(item.getId());
        itemBackup.setTask(item.getTask());
        itemBackup.setStatus(item.getStatus());
        // Copy other fields if they exist:
        // itemBackup.setExecutionCounter(item.getExecutionCounter());

        todoList.remove(position);
        notifyItemRemoved(position);

        executor.execute(() -> {
            try {
                db.openDatabase();
                db.deleteTask(item.getId());
                flagStates.remove(item.getId());
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (position <= todoList.size()) {
                        todoList.add(position, itemBackup);
                        notifyItemInserted(position);
                    }
                });
            }
        });
    }

    public void editItem(int position) {
        if (todoList == null || position < 0 || position >= todoList.size()) return;

        MainActivity activity = activityRef.get();
        if (activity == null) return;

        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public boolean getFlagState(int itemId) {
        return flagStates.getOrDefault(itemId, false);
    }

    public void setFlagState(int itemId, boolean flag) {
        flagStates.put(itemId, flag);
    }

    // Public method for handling item clicks
    public void handleItemClick(TextView counterView, ShapeableImageView achievementIcon,
                                int executionCount, boolean flag) {
        if (executionCount >= 0 && flag) {
            counterView.setText(String.valueOf(executionCount));
            counterView.setVisibility(View.VISIBLE);

            if (executionCount >= 10) {
                setupAchievement(counterView, achievementIcon,
                        R.drawable.counter_gold_background,
                        android.R.drawable.btn_star_big_on,
                        R.color.achievement_gold);
            } else if (executionCount >= 5) {
                setupAchievement(counterView, achievementIcon,
                        R.drawable.counter_silver_background,
                        android.R.drawable.btn_star_big_on,
                        R.color.achievement_silver);
            } else if (executionCount >= 3) {
                setupAchievement(counterView, achievementIcon,
                        R.drawable.counter_bronze_background,
                        android.R.drawable.btn_star_big_on,
                        R.color.achievement_bronze);
            } else {
                counterView.setBackgroundResource(R.drawable.counter_default_background);
                achievementIcon.setVisibility(View.GONE);
            }
        } else {
            counterView.setVisibility(View.GONE);
            achievementIcon.setVisibility(View.GONE);
        }
    }

    int getCounter (int id) {
        return db.getCounter(id);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        ShapeableImageView imageView;
        TextView counter;
        View v;
        ImageButton iv_btn;

        ViewHolder(View view) {
            super(view);
            v = view;
            task = view.findViewById(R.id.todoCheckBox);
            imageView = view.findViewById(R.id.achievement_icon);
            counter = view.findViewById(R.id.task_counter);
            iv_btn = view.findViewById(R.id.image_button);
        }


        public void bind(final ToDoModel item, int position, ToDoAdapter adapter) {
            if (item == null) return;


            iv_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get current flag state for this item
                    boolean currentFlag = adapter.getFlagState(item.getId());
                    boolean newFlag = !currentFlag; // Toggle state

                    int counterOfTasks = adapter.getCounter(item.getId());
                    // Save new state
                    adapter.setFlagState(item.getId(), newFlag);

                    adapter.handleItemClick(counter, imageView, counterOfTasks, newFlag);

                }
            });
        }

    }

    public void updateData(List<ToDoModel> data) {
        setTasks(data);
    }

    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        flagStates.clear();
        if (activityRef != null) {
            activityRef.clear();
        }
    }
}