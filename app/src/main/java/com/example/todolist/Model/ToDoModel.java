package com.example.todolist.Model;


import java.io.Serializable;
import java.util.Date;

// a data class
public class ToDoModel implements Serializable {
    private int id, status, executionCounter;
    private String task;
    private Date date;

    public ToDoModel () {}
    public ToDoModel(ToDoModel other) {
        this.id = other.id;
        this.task = other.task;
        this.status = other.status;
        this.executionCounter = other.executionCounter;
        this.date = other.date;

    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getExecutionCounter() {
        return executionCounter;
    }

    public void setExecutionCounter(int executionCounter) {
        this.executionCounter = executionCounter;
    }
}