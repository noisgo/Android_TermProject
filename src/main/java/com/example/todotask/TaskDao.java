package com.example.todotask;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Query("DELETE FROM task_table WHERE id = :taskId")
    void deleteTask(int taskId);

    @Query("SELECT * FROM task_table ORDER BY dueDate ASC")
    LiveData<List<Task>> getAllTasks();
}
