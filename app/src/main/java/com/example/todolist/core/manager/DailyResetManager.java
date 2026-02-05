package com.example.todolist.core.manager;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import com.example.todolist.core.util.DateUtil;

import com.example.todolist.data.model.ToDoModel;
import com.example.todolist.data.local.TaskRepository;

import java.util.List;

/**
 * Ensures tasks are reset once per day.
 * Reset occurs when the app is opened for the first time on a new day.
 */
public class DailyResetManager {

    private static final String SHARED_PREFS_NAME = "shared_prefs";
    private static final String LAST_RESET_KEY = "last_reset";

    public static void checkDailyReset(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        long today = DateUtil.normalizeDate(System.currentTimeMillis());
        long lastReset = prefs.getLong(LAST_RESET_KEY, 0);


        if (DateUtils.isToday(lastReset)) {
            return;
        }

        long yesterday = today - DateUtils.DAY_IN_MILLIS;


        TaskRepository repository = new TaskRepository(context);

        List<ToDoModel> yesterdayTasks =
                repository.getTodayTasks(yesterday);

        repository.insertCopyOfTasks(yesterdayTasks);
        repository.close();

        // save reset time
        prefs.edit()
                .putLong(LAST_RESET_KEY, today)
                .apply();

    }
}

