package com.example.todolist.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.todolist.data.model.ToDoModel;
import com.example.todolist.core.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String COUNTER = "counter";
    private static final String DATE = "date";

    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + STATUS + " INTEGER, " + COUNTER + " INTEGER, " + DATE + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task){
        long timestamp = DateUtil.normalizeDate(System.currentTimeMillis());

        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        cv.put(COUNTER, 0);
        cv.put(DATE, timestamp);
        db.insert(TODO_TABLE, null, cv);
    }

    public List<ToDoModel> getTodayTasks(long today) {

        openDatabase();
        List<ToDoModel> taskList = new ArrayList<>();

        long timeInSeconds = DateUtil.normalizeDate(today);

        Cursor cur = db.query(
                TODO_TABLE,
                null,
                DATE + " = ?",
                new String[]{String.valueOf(timeInSeconds)},
                null,
                null,
                null
        );

        if (cur.moveToFirst()) {
            do {
                ToDoModel task = new ToDoModel();
                task.setId(cur.getInt(cur.getColumnIndexOrThrow(ID)));
                task.setTask(cur.getString(cur.getColumnIndexOrThrow(TASK)));
                task.setStatus(cur.getInt(cur.getColumnIndexOrThrow(STATUS)));
                task.setExecutionCounter(cur.getInt(cur.getColumnIndexOrThrow(COUNTER)));
                taskList.add(task);
            } while (cur.moveToNext());
        }

        cur.close();
        return taskList;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public int getCounter(int id) {
        openDatabase();

        int counterValue = 0;

        Cursor cursor = db.rawQuery(
                "SELECT " + COUNTER + " FROM " + TODO_TABLE + " WHERE " + ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        if (cursor.moveToFirst()) {
            counterValue = cursor.getInt(0);
        }

        cursor.close();
        return counterValue;
    }

    public void incrementCounter(int id) {
        openDatabase();
        String sql = "UPDATE " + TODO_TABLE +
                " SET " + COUNTER + " = " + COUNTER + " + 1" +
                " WHERE " + ID + " = ?";
        db.execSQL(sql, new Object[]{id});

    }

    public void decrementCounter(int id) {
        openDatabase();
        String sql = "UPDATE " + TODO_TABLE +
                " SET " + COUNTER + " = CASE WHEN " + COUNTER + " > 0 THEN " + COUNTER + " - 1 ELSE 0 END" +
                " WHERE " + ID + " = ?";
        db.execSQL(sql, new Object[]{id});

    }


    public void deleteTask(int id){
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }


    public void insertCopyOfTasks(List<ToDoModel> list, long targetDate) {
        int size = list.size();
        ToDoModel model;
        for (int i = 0 ;i < size; i++) {
            model = list.get(i);
            ContentValues cv = new ContentValues();
            cv.put(TASK, model.getTask());
            cv.put(STATUS, 0);
            cv.put(COUNTER , model.getExecutionCounter());
            cv.put(DATE, DateUtil.normalizeDate(targetDate));
            db.insert(TODO_TABLE, null, cv);
        }
    }
    public List<ToDoModel> getTasksByDate(long date){
        List<ToDoModel> taskList = new ArrayList<>();

        Cursor cur = db.query(
                TODO_TABLE,
                null,
                DATE + " = ?",
                new String[]{String.valueOf(date)},
                null,
                null,
                null
        );

        if (cur.moveToFirst()) {
            do {
                ToDoModel task = new ToDoModel();
                task.setId(cur.getInt(cur.getColumnIndexOrThrow(ID)));
                task.setTask(cur.getString(cur.getColumnIndexOrThrow(TASK)));
                task.setStatus(cur.getInt(cur.getColumnIndexOrThrow(STATUS)));
                task.setExecutionCounter(cur.getInt(cur.getColumnIndexOrThrow(COUNTER)));
                taskList.add(task);
            } while (cur.moveToNext());
        }

        cur.close();
        return taskList;
    }

}