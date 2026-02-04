package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.Adapter.HistoryAdapter;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.DateUtil;
import com.example.todolist.repository.TaskRepository;

import java.util.Date;
import java.util.List;

public class ShowHistory extends AppCompatActivity {

    TextView textHistory;
    RecyclerView rv_History;
    HistoryAdapter adapter;
    TaskRepository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        repository = new TaskRepository(this);

        Intent intent = getIntent();
        String text = intent.getStringExtra("header");
        long time =  intent.getLongExtra("date", 0);
        List<ToDoModel> modelList = repository.getTasksByDate(DateUtil.normalizeDate(time));

        textHistory = findViewById(R.id.history_task);
        rv_History = findViewById(R.id.rv_history);

        textHistory.setText(text);
        adapter = new HistoryAdapter(modelList);
        rv_History.setLayoutManager(new LinearLayoutManager(this));
        rv_History.setHasFixedSize(true);
        rv_History.setAdapter(adapter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        repository.close();
    }
}