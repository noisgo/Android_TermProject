package com.example.todotask;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    static TaskDatabase taskDatabase;
    private TaskViewModel taskViewModel;
    private CheckBox checkBoxGlobalAlarm;
    private AlarmManager alarmManager;
    private NotificationManager notificationManager;
    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM = 1001;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskDatabase = TaskDatabase.getInstance(this);
        loadTasks();

        // 알람 매니저 초기화
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
        // 디바이스 재부팅 시 알람 재설정을 위한 BroadcastReceiver 등록
        registerBootCompletedReceiver();

        System.out.println("tasks:"+taskDatabase.taskDao().getAllTasks().toString());
        // 리스너----------------------------------------------------------
        // '+' 이미지뷰 클릭 리스너 - 편집 뷰로
        ImageView imageAddTask = findViewById(R.id.image_view_edit_task);
        imageAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
                startActivity(intent);
            }
        });

        // 제출 버튼 클릭 리스너 - 사캠으로
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

        //알람 버튼 리스너
        checkBoxGlobalAlarm = findViewById(R.id.check_box_global_alarm);
        checkBoxGlobalAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = checkBoxGlobalAlarm.isChecked();
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!alarmManager.canScheduleExactAlarms()) {
                            requestScheduleExactAlarmPermission();
                        } else {
                            setGlobalAlarm(true);
                            Toast.makeText(MainActivity.this, "과제 제출 알림이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        setGlobalAlarm(true);
                        Toast.makeText(MainActivity.this, "과제 제출 알림이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    setGlobalAlarm(false);
                    Toast.makeText(MainActivity.this, "과제 제출 알림이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // RecyclerView 및 어댑터 초기화
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // LayoutManager 설정

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList); // 어댑터 설정
        recyclerView.setAdapter(taskAdapter); // 어댑터 설정
    }

    //'...' 이미지뷰 눌렀을때 - 편집/삭제 메뉴
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.task_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_edit) {
            // Edit 버튼이 클릭되었을 때 편집으로 이동
            Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
            startActivity(intent);
            return true; // true를 반환하여 이벤트를 소비
        } else if (id == R.id.menu_delete) {
            Task selectedTask = taskAdapter.getSelectedTask();
            if (selectedTask != null) {
                deleteTask(selectedTask); // 선택된 과제 삭제
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show(); // '삭제되었습니다' 토스트 팝업
            } else {
                Toast.makeText(this, "No task selected", Toast.LENGTH_SHORT).show(); // 선택된 과제가 없을 때 토스트 팝업
            }
            return true; // true를 반환하여 이벤트를 소비
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

    // 선택된 과제를 삭제하는 메서드
    private void deleteTask(Task task) {
        if (task != null) {
            System.out.println("before:"+taskDatabase.taskDao().getAllTasks().toString());
            taskDatabase.taskDao().deleteTask(task); // ViewModel을 통해 과제 삭제
            System.out.println("after:"+taskDatabase.taskDao().getAllTasks().toString());

        }else{
            System.out.println("null!");

        }
        loadTasks();// Task 삭제 후 UI 업데이트
    }

    // 알람
    /**
     * 전체 과제에 대한 알람 설정 또는 취소
     *
     * @param enable 알람 설정 여부
     */
    private void setGlobalAlarm(boolean enable) {
        taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                for (Task task : tasks) {
                    if (enable) {
                        setAlarm(task);
                    } else {
                        cancelAlarm(task);
                    }
                }
            }
        });
    }

    /**
     * 특정 과제에 대한 알람 설정
     *
     * @param task 알람을 설정할 과제
     */
    private void setAlarm(Task task) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            calendar.setTime(dateFormat.parse(task.getDueDate()));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("taskTitle", task.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // 알람 설정 로그 추가
        Log.d("AlarmDebug", "Setting alarm for task: " + task.getTitle());
    }

    /**
     * 특정 과제에 대한 알람 취소
     *
     * @param task 알람을 취소할 과제
     */
    private void cancelAlarm(Task task) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * 알림 채널 생성 (Android 8.0 이상 필요)
     */
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("TASK_ALARM_CHANNEL", "Task Alarms", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for task alarms");
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 정확한 알람 설정 권한 요청
     */
    private void requestScheduleExactAlarmPermission() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        startActivityForResult(intent, REQUEST_CODE_SCHEDULE_EXACT_ALARM);
    }

    // BroadcastReceiver를 등록하는 메서드
    private void registerBootCompletedReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        BroadcastReceiver bootCompletedReceiver = new BootCompletedReceiver();
        this.registerReceiver(bootCompletedReceiver, filter);
    }

    // 알람을 다시 설정하는 BroadcastReceiver 정의
    public static class BootCompletedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                // 앱이 재부팅된 경우 알람을 다시 설정
                // 여기서 알람을 다시 설정하는 로직을 호출하면 됩니다.
                // 예: MainActivity.setGlobalAlarm(true);
            }
        }
    }


}
