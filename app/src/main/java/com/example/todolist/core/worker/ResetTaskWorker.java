package com.example.todolist.core.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.todolist.core.manager.DailyResetManager;
import com.example.todolist.data.model.ToDoModel;

import java.util.List;

public class ResetTaskWorker extends Worker {
    List<ToDoModel> list;

    public ResetTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {
           DailyResetManager.checkDailyReset(getApplicationContext());
            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }


    }
}
