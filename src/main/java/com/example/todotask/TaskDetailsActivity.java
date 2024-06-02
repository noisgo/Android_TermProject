package com.example.todotask;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class TaskDetailsActivity extends AppCompatActivity {

    private EditText editTextTaskTitle;
    private Button buttonSetDueDate;
    private TextView textViewDueDate;
    private Button buttonAddSubtask;
    private Button buttonSaveTask;
    private Button buttonDeleteTask;
    private TaskDatabase taskDatabase;

    private Calendar dueDate;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        editTextTaskTitle = findViewById(R.id.edit_text_task_title);
        buttonSetDueDate = findViewById(R.id.button_set_deadline);
        textViewDueDate = findViewById(R.id.text_view_due_date);
        //buttonAddSubtask = findViewById(R.id.button_add_subtask);
        buttonSaveTask = findViewById(R.id.button_save_task);
        buttonDeleteTask = findViewById(R.id.button_delete_task);

        // 데이터베이스 인스턴스 초기화
        taskDatabase = TaskDatabase.getInstance(this);

        // 전달된 task 객체를 가져옴 (편집 모드인 경우)
        if (getIntent().hasExtra("task")) {
            task = (Task) getIntent().getSerializableExtra("task");
            editTextTaskTitle.setText(task.getTitle());
            textViewDueDate.setText(task.getDueDate());
        }

        // 과제 저장 버튼 클릭시
        buttonSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });
        dueDate = Calendar.getInstance();

        if (task != null) {
            editTextTaskTitle.setText(task.getTitle());
            textViewDueDate.setText(task.getDueDate());
            // Parse the date and set the Calendar
        }

        buttonSetDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        buttonSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });

        buttonDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dueDate.set(year, month, dayOfMonth);
                        textViewDueDate.setText("Due Date: " + year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // 과제 저장
    private void saveTask() {
        String title = editTextTaskTitle.getText().toString();
        String dueDateString = textViewDueDate.getText().toString();

        // 제목과 날짜가 비어 있는지 확인
        if (title.isEmpty() || dueDateString.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // 새로운 과제를 추가하는 경우
        if (task == null) {
            task = new Task(title, dueDateString, false, 0);
            taskDatabase.taskDao().insertTask(task); // 데이터베이스에 새로운 과제 저장
        } else {
            task.setTitle(title);
            task.setDueDate(dueDateString);
            taskDatabase.taskDao().updateTask(task); // 데이터베이스에 기존 과제 업데이트
        }

        setResult(RESULT_OK);
        finish(); // 액티비티 종료
    }

    private void deleteTask() {
        // Handle task deletion
        finish();
    }
}
