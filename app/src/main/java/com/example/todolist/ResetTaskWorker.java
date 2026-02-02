package com.example.todolist;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.DateUtil;
import com.example.todolist.repository.TaskRepository;

import java.util.List;

public class ResetTaskWorker extends Worker {
    List<ToDoModel> list;

    public ResetTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        TaskRepository repository = new TaskRepository(getApplicationContext());

        try {
           DailyResetManager.checkDailyReset(getApplicationContext());
            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        } finally {
            repository.close();
        }


    }
}
