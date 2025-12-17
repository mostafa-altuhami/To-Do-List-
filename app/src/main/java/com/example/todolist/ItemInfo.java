package com.example.todolist;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Adapter.TasksInfoAdapter;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.DatabaseHandler;

import java.util.List;

public class ItemInfo extends AppCompatActivity {
    RecyclerView allTasks_rv;
    TasksInfoAdapter adapter;
    DatabaseHandler db;
    List<ToDoModel> toDoModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+
            getWindow().setDecorFitsSystemWindows(false);
            getWindow().getInsetsController().hide(android.view.WindowInsets.Type.statusBars());
        } else {
            // For older versions
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_item_info);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        db = new DatabaseHandler(getBaseContext());
        toDoModelList = db.getAllTasks();
        allTasks_rv = findViewById(R.id.allTasks_rv);
        adapter = new TasksInfoAdapter(toDoModelList);
        allTasks_rv.setLayoutManager(new LinearLayoutManager(this));
        allTasks_rv.setHasFixedSize(true);
        allTasks_rv.setAdapter(adapter);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Hide only status bar, keep navigation buttons
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}