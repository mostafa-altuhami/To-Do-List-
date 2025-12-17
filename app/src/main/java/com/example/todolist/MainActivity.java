package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Adapter.ToDoAdapter;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// the Main class
public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    private DatabaseHandler db;

    ImageView statistics_iv;
    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private BroadcastReceiver updateReceiver;
    private TextView taskText, counter;
    ShapeableImageView habit;
    private String dayOfWeek, dayOfWeekForHistory;
    SimpleDateFormat simpleDateFormat;
    private List<ToDoModel> taskList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        statistics_iv = findViewById(R.id.statistics);

        statistics_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this, ItemInfo.class);
                startActivity(intent);
            }
        });

        db = new DatabaseHandler(this);
        db.openDatabase();
        taskText = findViewById(R.id.tasksText);
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        taskList = db.getAllTasks();
        //Collections.reverse(taskList);
        tasksAdapter = new ToDoAdapter(db, MainActivity.this, taskList);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        tasksRecyclerView.setAdapter(tasksAdapter);

        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tasksAdapter.updateData(getData());
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(updateReceiver, new IntentFilter("UPDATE_RECYCLERVIEW"), Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(updateReceiver, new IntentFilter("UPDATE_RECYCLERVIEW"));
        }

        scheduleDailyReset(this);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        fab = findViewById(R.id.fab);


        // Collections.reverse(taskList);

        tasksAdapter.setTasks(taskList);
        simpleDateFormat = new SimpleDateFormat("EEEE");

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dayOfWeekForHistory = simpleDateFormat.format(calendar.getTime());

        String dateHistory = dayOfWeekForHistory + ": " + day + "-" + (month + 1) + "-" + year;
        taskText.setText(dateHistory);


        taskText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                DatePickerDialog pickerDialog = new DatePickerDialog(MainActivity.this,
                        R.style.DatePickerDialogTheme
                        ,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(i, i1, i2, 0, 0, 0);
                        Date date = selectedDate.getTime();
                        List<ToDoModel> listOfUpdatedTasks = db.getAllTasksByDate(date);
                        dayOfWeek = simpleDateFormat.format(selectedDate.getTime());
                        String fullDateText = dayOfWeek + ": " + i2 + "-" + (i1 + 1) + "-" + i;
                        Intent intent = new Intent(MainActivity.this, ShowHistory.class);
                        intent.putExtra("text", String.valueOf(fullDateText));
                        intent.putExtra("list", (Serializable) listOfUpdatedTasks);
                        startActivity(intent);
                    }
                },year, month, day);
                pickerDialog.show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    private List<ToDoModel> getData() {
        return db.getAllTasks();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleDailyReset(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ResetCheckboxReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.SECOND,5);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis())
            calendar.add(Calendar.DAY_OF_MONTH, 1);

//       alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,pendingIntent);
        //Log.d("MainActivity", "alarm scheduled for: "  + calendar.getTime().toString());

     }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateReceiver);
        tasksAdapter.cleanup();
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        //Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }


}