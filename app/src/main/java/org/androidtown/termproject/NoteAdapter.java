package org.androidtown.termproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// RecyclerView.Adapter를 상속받아 NoteAdapter 클래스 정의
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private static final String TAG = "NoteAdapter";

    // ToDo 리스트 아이템을 저장할 ArrayList
    ArrayList<Note> items = new ArrayList<>();

    // ViewHolder를 생성하는 메서드
    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 사용하여 todo_item.xml 레이아웃을 인플레이션
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.todo_item, parent, false);

        // 생성한 뷰를 ViewHolder에 전달하여 반환
        return new ViewHolder(itemView);
    }

    // 각 아이템에 데이터를 바인딩하는 메서드
    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        // 현재 위치의 아이템을 가져옴
        Note item = items.get(position);

        // ViewHolder에 아이템 데이터를 설정
        holder.setItem(item);
        holder.setLayout();
    }

    // 아이템의 총 개수를 반환하는 메서드
    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder 클래스 정의: RecyclerView의 각 아이템 뷰를 관리
    static class ViewHolder extends RecyclerView.ViewHolder {

        // 레이아웃 및 위젯 변수 선언
        LinearLayout layoutTodo;
        CheckBox checkBox;
        Button deleteButton;

        // ViewHolder의 생성자: itemView를 초기화
        public ViewHolder(View itemView) {
            super(itemView);

            // 레이아웃 및 위젯 변수 초기화
            layoutTodo = itemView.findViewById(R.id.layoutTodo);
            checkBox = itemView.findViewById(R.id.checkBox);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            // 삭제 버튼 클릭 리스너 설정
            deleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // CheckBox의 텍스트 가져오기
                    String TODO = (String) checkBox.getText();
                    // 삭제 메서드 호출
                    deleteToDo(TODO);
                    // 삭제 완료 메시지 표시
                    Toast.makeText(v.getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }

                Context context;

                // ToDo 항목을 삭제하는 메서드
                private void deleteToDo(String TODO) {
                    // 테이블에서 데이터를 삭제하는 SQL 문
                    String deleteSql = "delete from " + NoteDatabase.TABLE_NOTE + " where TODO = '" + TODO + "'";
                    NoteDatabase database = NoteDatabase.getInstance(context);
                    // SQL 문 실행
                    database.execSQL(deleteSql);
                }
            });
        }

        // CheckBox의 텍스트를 설정하는 메서드
        public void setItem(Note item) {
            checkBox.setText(item.getTodo());
        }

        // 아이템 레이아웃을 표시하는 메서드
        public void setLayout() {
            layoutTodo.setVisibility(View.VISIBLE);
        }
    }

    // 아이템 리스트를 설정하는 메서드
    public void setItems(ArrayList<Note> items) {
        this.items = items;
    }
}
