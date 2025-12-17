package com.example.todolist;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import com.example.todolist.Adapter.HistoryAdapter;
import com.example.todolist.Adapter.ToDoAdapter;
import com.example.todolist.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class ShowHistory extends AppCompatActivity {

    TextView textHistory;
    RecyclerView rv_History;
    HistoryAdapter adapter;

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

        // Hide only status bar, keep navigation bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);


        setContentView(R.layout.activity_show_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent intent = getIntent();
        String text = intent.getStringExtra("text");
        Log.d("DEBUG", "Received text: " + text);
        List<ToDoModel> modelList = (List<ToDoModel>) intent.getSerializableExtra("list");

        textHistory = findViewById(R.id.history_task);
        rv_History = findViewById(R.id.rv_history);

        textHistory.setText(text);
        adapter = new HistoryAdapter(modelList);
        rv_History.setLayoutManager(new LinearLayoutManager(this));
        rv_History.setHasFixedSize(true);
        rv_History.setAdapter(adapter);


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