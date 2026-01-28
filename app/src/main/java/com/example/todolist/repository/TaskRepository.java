package com.example.todolist.repository;

import android.content.Context;

import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.DatabaseHandler;

import java.util.Date;
import java.util.List;

public class TaskRepository {

    private final DatabaseHandler db;

    public TaskRepository(Context context) {
        db = new DatabaseHandler(context);
        db.openDatabase();
    }

    public List<ToDoModel> getAllTasks() {
        return db.getAllTasks();
    }


    public List<ToDoModel> getAllTasksByDate(Date date) {
        return db.getAllTasksByDate(date);
    }

    public void incrementCounter(int id) {
        db.incrementCounter(id);
    }

    public void decrementCounter(int id) {
        db.decrementCounter(id);
    }

    public void updateStatus(int id, int status) {
        db.updateStatus(id, status);
    }

    public int getCounter(int id) {
        return db.getCounter(id);
    }

    public void deleteTask(int id) {
        db.deleteTask(id);
    }

    public void close() {
        db.close();
    }

}
