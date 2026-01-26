package com.example.todolist;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.DatabaseHandler;
import java.util.List;

public class ResetTaskWorker extends Worker {
    List<ToDoModel> list;

    public ResetTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        list = db.getAllTasks();
        db.resetAllCheckboxes(getApplicationContext());
        db.insertCopyOfTasks(list);
        db.close();

        Log.d("RESET_TASKS", "finished");
        return Result.success();
    }
}
