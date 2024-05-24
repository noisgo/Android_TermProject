package org.androidtown.termproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";  // 로그 태그

    Fragment mainFragment;  // 메인 프래그먼트 참조
    EditText inputToDo;  // 사용자 입력을 받을 EditText 참조
    Context context;  // 컨텍스트 참조

    public static NoteDatabase noteDatabase = null;  // 데이터베이스 인스턴스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // 레이아웃 파일 설정

        mainFragment = new MainFragment();  // 메인 프래그먼트 초기화

        // 프래그먼트 매니저를 사용하여 컨테이너에 프래그먼트 추가
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();

        Button saveButton = findViewById(R.id.saveButton);  // 레이아웃에서 saveButton 찾기
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // 버튼 클릭 시 호출될 메서드
                saveToDo();

                // 작업이 완료되었음을 사용자에게 알림
                Toast.makeText(getApplicationContext(),"추가되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        openDatabase();  // 데이터베이스 열기
    }

    // 사용자가 입력한 할 일을 저장하는 메서드
    private void saveToDo(){
        inputToDo = findViewById(R.id.inputToDo);  // 레이아웃에서 inputToDo 찾기

        // EditText에 입력된 텍스트 가져오기
        String todo = inputToDo.getText().toString();

        // 테이블에 값을 추가하는 SQL 구문 작성
        String sqlSave = "insert into " + NoteDatabase.TABLE_NOTE + " (TODO) values (" +
                "'" + todo + "')";

        // 데이터베이스 인스턴스를 가져와 SQL 구문 실행
        NoteDatabase database = NoteDatabase.getInstance(context);
        database.execSQL(sqlSave);

        // 저장 후 EditText 필드를 초기화
        inputToDo.setText("");
    }

    // 데이터베이스를 여는 메서드
    public void openDatabase() {
        // 데이터베이스가 열려 있다면 닫기
        if (noteDatabase != null) {
            noteDatabase.close();
            noteDatabase = null;
        }

        // 데이터베이스 인스턴스를 생성하고 열기
        noteDatabase = NoteDatabase.getInstance(this);
        boolean isOpen = noteDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Note database is open.");  // 데이터베이스가 열렸음을 로그에 기록
        } else {
            Log.d(TAG, "Note database is not open.");  // 데이터베이스가 열리지 않았음을 로그에 기록
        }
    }

    // 액티비티가 파괴될 때 호출되는 메서드
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 데이터베이스가 열려 있다면 닫기
        if (noteDatabase != null) {
            noteDatabase.close();
            noteDatabase = null;
        }
    }
}
