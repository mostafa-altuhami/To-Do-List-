package com.example.todolist;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import com.example.todolist.Utils.DateUtil;

import com.example.todolist.Model.ToDoModel;
import com.example.todolist.repository.TaskRepository;

import java.util.List;

/**
 * Ensures tasks are reset once per day.
 * Reset occurs when the app is opened for the first time on a new day.
 */
public class DailyResetManager {

    private static final String SHARED_PREFS_NAME = "shared_prefs";
    private static final String LAST_RESET_KEY = "last_reset";

    public static boolean checkDailyReset(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        long lastReset = sharedPreferences.getLong(LAST_RESET_KEY, 0);

        if (!DateUtils.isToday(lastReset)) {
            TaskRepository repository = new TaskRepository(context);
            List<ToDoModel> list = repository.getTodayTasks(DateUtil.normalizeDate(System.currentTimeMillis()));
            repository.resetAllCheckboxes(context);
            repository.insertCopyOfTasks(list);
            sharedPreferences.edit()
                    .putLong(LAST_RESET_KEY, System.currentTimeMillis())
                    .apply();
            repository.close();
            return true;

        }

        return false;
    }
}
