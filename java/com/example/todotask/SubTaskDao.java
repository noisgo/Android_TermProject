package com.example.todotask;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SubTaskDao {
    @Insert
    void insertSubTask(SubTask subTask);

    @Update
    void updateSubTask(SubTask subTask);

    @Delete
    void deleteSubTask(SubTask subTask);

    @Query("DELETE FROM subtask_table WHERE id = :subTaskId")
    void deleteSubTaskById(int subTaskId);

    @Query("SELECT * FROM subtask_table WHERE taskId = :taskId")
    LiveData<List<SubTask>> getSubTasksForTask(int taskId);

    @Query("SELECT * FROM subtask_table")
    LiveData<List<SubTask>> getAllSubTasks();
}
