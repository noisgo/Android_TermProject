package com.example.todotask;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private Task selectedTask; // 선택된 과제를 저장할 변수

    // 생성자를 통해 데이터 전달
    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    // ViewHolder 객체를 생성하여 리턴
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list, parent, false);
        return new TaskViewHolder(view);
    }

    // ViewHolder안의 내용을 position에 해당되는 데이터로 교체
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        selectedTask = taskList.get(position);

        holder.titleTextView.setText(task.getTitle());
        holder.dueDateTextView.setText(task.getDueDate());
        holder.statusProgressBar.setProgress(task.getProgress()); // 과제의 세부 과제 완성도에 따라 프로그레스바 설정

        // 이미지뷰를 클릭할 때 컨텍스트 메뉴가 표시되도록 설정
        holder.editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(), v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.task_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_edit) {
                            Intent intent = new Intent(holder.itemView.getContext(), TaskDetailsActivity.class);
                            intent.putExtra("task", task);
                            holder.itemView.getContext().startActivity(intent);
                            return true;
                        }  else if (item.getItemId() == R.id.menu_delete) {
                            // 선택된 과제 삭제
                            Task selectedTask = getSelectedTask();
                            if (selectedTask != null) {
                                deleteTask(selectedTask);
                                Toast.makeText(holder.itemView.getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged(); // 데이터 변경을 RecyclerView에 알려 UI를 업데이트합니다.
    }

    // 선택된 과제를 반환하는 메서드
    public Task getSelectedTask() {
        return selectedTask;
    }




    // 전체 데이터의 갯수를 리턴
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // TaskAdapter 클래스에 삭제 메서드 추가
    public void deleteTask(Task task) {
        taskList.remove(task);
        Runnable r=new Runnable() {
            @Override
            public void run() {
                MainActivity.taskDatabase.taskDao().deleteTask(task);
            }
        };
        Thread t=new Thread(r);
        t.start();

        notifyDataSetChanged(); // 데이터 변경을 RecyclerView에 알려 UI를 업데이트합니다.
    }

    // 뷰홀더 클래스
    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        ProgressBar statusProgressBar;
        TextView titleTextView;
        TextView dueDateTextView;
        ImageView editImageView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            statusProgressBar = itemView.findViewById(R.id.progressBar_status);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            dueDateTextView = itemView.findViewById(R.id.text_view_due_date);
            editImageView = itemView.findViewById(R.id.image_view_edit_task);
        }
    }

}