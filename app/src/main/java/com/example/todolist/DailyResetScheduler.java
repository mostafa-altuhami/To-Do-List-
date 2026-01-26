package com.example.todolist;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DailyResetScheduler {

    public static void scheduleDailyReset(Context context) {
        long initialDelay = calculateInitialDelay();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                ResetTaskWorker.class,
                1,
                TimeUnit.DAYS
        ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

//        OneTimeWorkRequest test = new OneTimeWorkRequest.Builder(
//                ResetTaskWorker.class)
//                .setInitialDelay(10, TimeUnit.SECONDS)
//                        .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "delay_reset_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
        );


    }

    public static long calculateInitialDelay() {
        Calendar now = Calendar.getInstance();
        Calendar nextReset = Calendar.getInstance();

        nextReset.set(Calendar.HOUR_OF_DAY, 0);
        nextReset.set(Calendar.MINUTE, 0);
        nextReset.set(Calendar.SECOND, 0);
        nextReset.set(Calendar.MILLISECOND, 0);

        if (nextReset.getTimeInMillis() <= now.getTimeInMillis()){
            nextReset.add(Calendar.DAY_OF_MONTH, 1);
        }

        return nextReset.getTimeInMillis() - now.getTimeInMillis();
    }
}
