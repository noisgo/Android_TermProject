package com.example.todotask;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository taskRepository;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<SubTask>> allSubTasks;

    public TaskViewModel(Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        allTasks = taskRepository.getAllTasks();
        allSubTasks = taskRepository.getAllSubTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<SubTask>> getAllSubTasks() {
        return allSubTasks;
    }

    public LiveData<List<SubTask>> getSubTasksForTask(int taskId) {
        return taskRepository.getSubTasksForTask(taskId);
    }

    public void insertTask(Task task) {
        taskRepository.insertTask(task);
    }

    public void updateTask(Task task) {
        taskRepository.updateTask(task);
    }

    public void deleteTask(Task task) {
        taskRepository.deleteTask(task);
    }

    public void insertSubTask(SubTask subTask) {
        taskRepository.insertSubTask(subTask);
    }

    public void updateSubTask(SubTask subTask) {
        taskRepository.updateSubTask(subTask);
    }

    public void deleteSubTask(SubTask subTask) {
        taskRepository.deleteSubTask(subTask);
    }
}
