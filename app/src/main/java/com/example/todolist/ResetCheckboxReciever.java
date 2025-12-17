package com.example.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.DatabaseHandler;

import java.util.Date;
import java.util.List;

public class ResetCheckboxReciever extends BroadcastReceiver {
    List<ToDoModel> list;
    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            Log.d("ResetCheckbox", "Alarm trigged at: " + new Date().toString());
            DatabaseHandler db = new DatabaseHandler(context);
            list = db.getAllTasks();
            db.resetAllCheckboxes(context);
            db.insertCopyOfTasks(list);
            Intent updateIntent = new Intent("UPDATE_RECYCLERVIEW");
            context.sendBroadcast(updateIntent);
            Log.d("ResetCheckbox", "broadcast sent to update rv");
        } catch (Exception e){
            Log.d("ResetCheckboxe", "Error in onRecieve " + e.getMessage());
            e.printStackTrace();
        }

    }
}
