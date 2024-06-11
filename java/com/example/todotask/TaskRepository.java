package com.example.todotask;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {

    private TaskDao taskDao;
    private SubTaskDao subTaskDao;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<SubTask>> allSubTasks;

    public TaskRepository(Application application) {
        TaskDatabase database = TaskDatabase.getInstance(application);
        taskDao = database.taskDao();
        subTaskDao = database.subTaskDao();
        allTasks = taskDao.getAllTasks();
        allSubTasks = subTaskDao.getAllSubTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<SubTask>> getAllSubTasks() {
        return allSubTasks;
    }

    public LiveData<List<SubTask>> getSubTasksForTask(int taskId) {
        return subTaskDao.getSubTasksForTask(taskId);
    }

    public void insertTask(Task task) {
        new InsertTaskAsyncTask(taskDao).execute(task);
    }

    public void updateTask(Task task) {
        new UpdateTaskAsyncTask(taskDao).execute(task);
    }

    public void deleteTask(Task task) {
        new DeleteTaskAsyncTask(taskDao).execute(task);
    }

    public void insertSubTask(SubTask subTask) {
        new InsertSubTaskAsyncTask(subTaskDao).execute(subTask);
    }

    public void updateSubTask(SubTask subTask) {
        new UpdateSubTaskAsyncTask(subTaskDao).execute(subTask);
    }

    public void deleteSubTask(SubTask subTask) {
        new DeleteSubTaskAsyncTask(subTaskDao).execute(subTask);
    }

    private static class InsertTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private InsertTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.insertTask(tasks[0]);
            return null;
        }
    }

    private static class UpdateTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private UpdateTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.updateTask(tasks[0]);
            return null;
        }
    }

    private static class DeleteTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private DeleteTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.deleteTaskById(tasks[0].getId());
            return null;
        }
    }

    private static class InsertSubTaskAsyncTask extends AsyncTask<SubTask, Void, Void> {
        private SubTaskDao subTaskDao;

        private InsertSubTaskAsyncTask(SubTaskDao subTaskDao) {
            this.subTaskDao = subTaskDao;
        }

        @Override
        protected Void doInBackground(SubTask... subTasks) {
            subTaskDao.insertSubTask(subTasks[0]);
            return null;
        }
    }

    private static class UpdateSubTaskAsyncTask extends AsyncTask<SubTask, Void, Void> {
        private SubTaskDao subTaskDao;

        private UpdateSubTaskAsyncTask(SubTaskDao subTaskDao) {
            this.subTaskDao = subTaskDao;
        }

        @Override
        protected Void doInBackground(SubTask... subTasks) {
            subTaskDao.updateSubTask(subTasks[0]);
            return null;
        }
    }

    private static class DeleteSubTaskAsyncTask extends AsyncTask<SubTask, Void, Void> {
        private SubTaskDao subTaskDao;

        private DeleteSubTaskAsyncTask(SubTaskDao subTaskDao) {
            this.subTaskDao = subTaskDao;
        }

        @Override
        protected Void doInBackground(SubTask... subTasks) {
            subTaskDao.deleteSubTaskById(subTasks[0].getId());
            return null;
        }
    }
}
