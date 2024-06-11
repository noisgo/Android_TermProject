package com.example.todotask;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(tableName = "subtask_table",
        foreignKeys = @ForeignKey(entity = Task.class,
                parentColumns = "id",
                childColumns = "taskId",
                onDelete = ForeignKey.CASCADE))

public class SubTask {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int taskId;
    private String contents;
    private boolean isChecked;

    // 생성자
    public SubTask(int taskId, String contents, boolean isChecked) {
        this.taskId = taskId;
        this.contents = contents;
        this.isChecked = isChecked;
    }

    // Getter 및 Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getContents() {
        return contents;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }

    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}

