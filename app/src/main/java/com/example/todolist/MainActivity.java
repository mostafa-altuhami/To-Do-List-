package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.Adapter.ToDoAdapter;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.repository.TaskRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    private TaskRepository repository;

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private TextView taskText;
    SimpleDateFormat sdf;
    private List<ToDoModel> taskList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();


        repository = new TaskRepository(this);
        sdf = new SimpleDateFormat("EEEE");

        taskText = findViewById(R.id.tasksText);
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        fab = findViewById(R.id.fab);

        taskList = repository.getAllTasks();

        tasksAdapter = new ToDoAdapter(repository, MainActivity.this, taskList);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(tasksAdapter);




        DailyResetScheduler.scheduleDailyReset(this);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);


        tasksAdapter.setTasks(taskList);

        setupDateHeader();
        setupClicks();

    }

    private void setupDateHeader() {

       Calendar calendar = Calendar.getInstance();

        String text = sdf.format(calendar.getTime()) + ": " +
                calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                (calendar.get(Calendar.MONTH) + 1) + "-" +
                calendar.get(Calendar.YEAR);

        taskText.setText(text);
    }

    private void setupClicks () {

        fab.setOnClickListener(v ->
                AddNewTask.newInstance()
                        .show(getSupportFragmentManager(), AddNewTask.TAG));

        taskText.setOnClickListener(view -> {

            Calendar now = Calendar.getInstance();

            new DatePickerDialog(this,
                    R.style.DatePickerDialogTheme,
                    (v, year, month, day) -> {

                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, day, 0, 0, 0);

                        Date date = selected.getTime();
                        List<ToDoModel> history = repository.getAllTasksByDate(date);

                        String dateText = sdf.format(date) + ": " + selected.get(Calendar.DAY_OF_MONTH) + "-" +
                                (selected.get(Calendar.MONTH) + 1) + "-" + selected.get(Calendar.YEAR);


                        Intent intent = new Intent(this, ShowHistory.class);
                        intent.putExtra("header", dateText);
                        intent.putExtra("date", date);
                        startActivity(intent);
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)

            ).show();

        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        tasksAdapter.cleanup();
        repository.close();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = repository.getAllTasks();
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }


}