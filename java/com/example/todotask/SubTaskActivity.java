package com.example.todotask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class SubTaskActivity extends AppCompatActivity {

    private EditText editTextSubTaskContents;
    private Button buttonSaveSubTask;
    private ImageView imageViewDeleteSubtask;
    private Button delSubtask;
    public TaskViewModel taskViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        TaskViewModel taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        editTextSubTaskContents = findViewById(R.id.edit_text_subtask_contents);
        buttonSaveSubTask = findViewById(R.id.button_add_subtask);
        //imageViewDeleteSubtask = findViewById(R.id.image_view_delete_subtask);

        buttonSaveSubTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 세부 과제 제목을 가져옵니다.
                String subTaskContents = editTextSubTaskContents.getText().toString();

                // 결과를 담을 인텐트를 생성합니다.
                Intent resultIntent = new Intent();

                // 세부 과제 제목을 인텐트에 추가합니다.
                resultIntent.putExtra("subTaskContents", subTaskContents);

                // 액티비티 결과를 설정하고 현재 액티비티를 종료합니다.
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });




    }
}
