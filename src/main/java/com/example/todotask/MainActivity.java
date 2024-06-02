package com.example.todotask;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TaskDatabase taskDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // LayoutManager 설정

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList); // 어댑터 설정
        recyclerView.setAdapter(taskAdapter); // 어댑터 설정

        taskDatabase = TaskDatabase.getInstance(this);
        loadTasks();

        // 이미지뷰 클릭 리스너 설정
        ImageView imageAddTask = findViewById(R.id.image_view_edit_task);
        imageAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
                startActivity(intent);
            }
        });

        Button buttonGoToWebsite = findViewById(R.id.button_go_to_website);
        buttonGoToWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://cyber.gachon.ac.kr";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_edit) {
            // Edit 버튼이 클릭되었을 때의 동작을 여기에 구현합니다.
            Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
            startActivity(intent);
            return true; // true를 반환하여 이벤트를 소비
        } else if (id == R.id.menu_delete) {
            // Delete 버튼이 클릭되었을 때 해당하는 과제를 데이터에서 삭제
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 과제가 추가되었을 때 리사이클러뷰를 업데이트합니다.
            loadTasks();
        }
    }

    private void loadTasks() {
        taskDatabase.taskDao().getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> taskList) {
                // 변경 사항이 감지되면 리사이클러뷰를 업데이트합니다.
                taskAdapter.setTasks(taskList);
            }
        });
    }
}
