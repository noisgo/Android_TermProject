package com.example.todotask;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class TaskDetailsActivity extends AppCompatActivity {

    private SubTaskDatabase subTaskDatabase;
    private EditText editTextTaskTitle;
    private ImageView imageViewSetDueDate;
    private TextView textViewDueDate;
    private Button buttonSaveTask;
    private Button buttonDeleteTask;
    private ImageView imageViewPreviousTask;

    //세부과제
    private RecyclerView recyclerViewSubtasks;
    private SubTaskAdapter subtaskAdapter; // 어댑터 선언
    private List<SubTask> subtaskList; // Subtask 리스트 선언
    private int currentTaskId; // 현재 과제의 ID
    private EditText editTextSubtaskContents;
    private Button buttonAddSubtask;


    private Calendar dueDate;
    private Task task;
    static TaskViewModel taskViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        subTaskDatabase=SubTaskDatabase.getInstance(this);
        editTextTaskTitle = findViewById(R.id.edit_text_task_title);
        textViewDueDate = findViewById(R.id.text_view_due_date);
        editTextSubtaskContents = findViewById(R.id.edit_text_subtask_contents);

        // 뷰모델 초기화
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        // 인스턴스 초기화
        subTaskDatabase = SubTaskDatabase.getInstance(this);
        dueDate = Calendar.getInstance();

        // 전달된 task 객체를 가져옴 (편집 모드인 경우)
        if (getIntent().hasExtra("task")) {
            task = (Task) getIntent().getSerializableExtra("task");
            editTextTaskTitle.setText(task.getTitle());
            textViewDueDate.setText(task.getDueDate());
        }

        if (task != null) {
            editTextTaskTitle.setText(task.getTitle());
            textViewDueDate.setText(task.getDueDate());
            taskViewModel.getSubTasksForTask(task.getId()).observe(this, subTasks -> {
                subtaskList.clear();
                subtaskList.addAll(subTasks);
                subtaskAdapter.notifyDataSetChanged();
            });

        }

        /*리스너--------------------------------------------------------------*/
        // 이전 버튼 클릭 리스너 - 홈 뷰로
        imageViewPreviousTask = findViewById(R.id.image_view_previous_task);
        imageViewPreviousTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 완료 버튼 클릭 리스너 - 과제 저장 & 홈 뷰로
        buttonSaveTask = findViewById(R.id.button_save_task);
        buttonSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
                finish();
            }
        });

        // 과제 기한 이미지 뷰 리스너
        imageViewSetDueDate = findViewById(R.id.image_view_set_deadline);
        imageViewSetDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog();
            }
        });

        // 삭제 버튼 클릭 리스너
        buttonDeleteTask = findViewById(R.id.button_delete_task);
        buttonDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });

        // 세부과제 추가 버튼 클릭시
        buttonAddSubtask = findViewById(R.id.button_add_subtask);
        buttonAddSubtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubtask();
            }
        });
        /*--------------------------------------------------------------*/

        if (task != null) {
            editTextTaskTitle.setText(task.getTitle());
            textViewDueDate.setText(task.getDueDate());
            // Parse the date and set the Calendar
        }

        // RecyclerView 및 어댑터 초기화
        recyclerViewSubtasks = findViewById(R.id.recycler_view_subtasks);
        subtaskList = new ArrayList<>(); // 초기 Subtask 리스트 생성
        subtaskAdapter = new SubTaskAdapter(subtaskList); // 어댑터 초기화
        recyclerViewSubtasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSubtasks.setAdapter(subtaskAdapter);

        // 현재 과제의 ID를 가져오는 로직 추가
        currentTaskId = getIntent().getIntExtra("TASK_ID", -1); // 전달된 ID를 받거나 기본값 -1 설정

    }

    //제출 기한 설정
    private void showDateTimePickerDialog() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);
        int hour = currentDate.get(Calendar.HOUR_OF_DAY);
        int minute = currentDate.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dueDate.set(year, month, dayOfMonth);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(TaskDetailsActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        dueDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        dueDate.set(Calendar.MINUTE, minute);
                                        updateDateTimeTextView();
                                    }
                                }, hour, minute, false);

                        timePickerDialog.show();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
    private void updateDateTimeTextView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(dueDate.getTime());
        textViewDueDate.setText(formattedDate);
    }

    // 과제 저장
    private void saveTask() {
        String title = editTextTaskTitle.getText().toString();
        String dueDateString = textViewDueDate.getText().toString();

        // 디버그용 로그 메시지 출력
        Log.d("SaveTask", "Title: " + title + ", Due Date: " + dueDateString);

        // 제목과 날짜가 비어 있는지 확인
        if (title.isEmpty() || dueDateString.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (task == null) {
            task = new Task(title, dueDateString, false, 0);
            taskViewModel.insertTask(task); // ViewModel을 통해 새로운 과제 저장
        } else {
            task.setTitle(title);
            task.setDueDate(dueDateString);
            taskViewModel.updateTask(task); // ViewModel을 통해 기존 과제 업데이트
        }

        setResult(RESULT_OK);
        finish(); // 액티비티 종료
    }

    //과제 삭제
    private void deleteTask() {
        if (task != null) {
            taskViewModel.deleteTask(task); // ViewModel을 통해 과제 삭제
        }
        setResult(RESULT_OK);
        finish(); // 액티비티 종료
    }

    // 세부과제 추가
    private void addSubtask() {
        String subTaskContents = editTextSubtaskContents.getText().toString().trim();

        // 디버그용 로그 메시지 출력
        Log.d("SaveSubtask", "Title: " + subTaskContents);

        if (subTaskContents.isEmpty()) {
            Toast.makeText(this, "Subtask title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (task == null) {
            Toast.makeText(this, "Task is null, cannot add subtask", Toast.LENGTH_SHORT).show();
            return;
        }

        // 새로운 과제를 추가하는 경우
        SubTask subTask = new SubTask(task.getId(), subTaskContents, false);
        taskViewModel.insertSubTask(subTask); // ViewModel을 통해 세부 과제 저장
        editTextSubtaskContents.setText("");
    }

    // TODO: 세부과제 삭제
    public void deleteSubtask(SubTask subTask){
        if (subTask != null) {


            taskViewModel.deleteSubTask(subTask); // ViewModel을 통해 과제 삭제
        }
        setResult(RESULT_OK);
        finish(); // 액티비티 종료
    }



}
