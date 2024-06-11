package com.example.todotask;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "task_table")
public class Task implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String dueDate;
    private boolean isComplete;
    private int progress;

    public Task(String title, String dueDate, boolean isComplete, int progress) {
        this.title = title;
        this.dueDate = dueDate;
        this.isComplete = isComplete;
        this.progress = progress;
    }

    // Getter 및 Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDueDate() {
        return dueDate;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isComplete() {
        return isComplete;
    }
    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
}
