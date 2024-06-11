package com.example.todotask;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.todotask.SubTask;
import com.example.todotask.SubTaskDao;

import java.util.concurrent.Executor;

@Database(entities = {Task.class, SubTask.class}, version = 1, exportSchema = false)
public abstract class SubTaskDatabase extends RoomDatabase {
    private static SubTaskDatabase INSTANCE;

    public abstract SubTaskDao subTaskDao();

    public static synchronized SubTaskDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SubTaskDatabase.class, "subtask_database")
                    .build();
        }
        return INSTANCE;
    }
}