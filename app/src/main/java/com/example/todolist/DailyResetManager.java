package com.example.todolist;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;

import com.example.todolist.Utils.DatabaseHandler;

/**
 * Ensures tasks are reset once per day.
 * Reset occurs when the app is opened for the first time on a new day.
 */
public class DailyResetManager {

    private static final String SHARED_PREFS_NAME = "shared_prefs";
    private static final String LAST_RESET_KEY = "last_reset";

    public static void checkDailyReset(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        long lastReset = sharedPreferences.getLong(LAST_RESET_KEY, 0);

        if (!DateUtils.isToday(lastReset)) {
            DatabaseHandler db = new DatabaseHandler(context);
            db.resetAllCheckboxes(context);
            sharedPreferences.edit()
                    .putLong(LAST_RESET_KEY, System.currentTimeMillis())
                    .apply();
            db.close();
        }
    }
}
