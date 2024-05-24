package org.androidtown.termproject;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

// 메인 프래그먼트를 정의하는 클래스
public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    RecyclerView recyclerView; // RecyclerView를 정의
    NoteAdapter adapter; // 어댑터를 정의
    Context context; // 컨텍스트를 정의

    SwipeRefreshLayout swipeRefreshLayout; // 새로고침 레이아웃을 정의

    // 프래그먼트의 뷰를 생성하는 메서드
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // fragment_main 레이아웃을 인플레이션
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        // UI 초기화
        initUI(rootView);

        // 할 일 목록 데이터를 로드
        loadNoteListData();

        // 당겨서 새로고침 기능 설정
        swipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 데이터 새로고침
                loadNoteListData();
                // 새로고침 완료 상태로 설정
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    // UI를 초기화하는 메서드
    private void initUI(ViewGroup rootView) {

        // RecyclerView 연결
        recyclerView = rootView.findViewById(R.id.recyclerView);

        // LinearLayoutManager를 사용하여 RecyclerView 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // 어댑터 연결
        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);
    }

    // 할 일 목록 데이터를 로드하는 메서드
    public int loadNoteListData() {

        // 데이터를 가져오는 SQL 문 (id의 역순으로 정렬)
        String loadSql = "select _id, TODO from " + NoteDatabase.TABLE_NOTE + " order by _id desc";

        int recordCount = -1;
        NoteDatabase database = NoteDatabase.getInstance(context);

        if (database != null) {
            // Cursor를 사용하여 rawQuery 결과 저장
            Cursor outCursor = database.rawQuery(loadSql);

            // 데이터 레코드 수 가져오기
            recordCount = outCursor.getCount();

            // _id와 TODO가 담길 ArrayList 생성
            ArrayList<Note> items = new ArrayList<>();

            // Cursor를 사용하여 데이터를 하나씩 추가
            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();

                int _id = outCursor.getInt(0); // 첫 번째 열의 데이터 (ID)
                String todo = outCursor.getString(1); // 두 번째 열의 데이터 (TODO)
                items.add(new Note(_id, todo)); // Note 객체를 생성하여 리스트에 추가
            }
            outCursor.close(); // Cursor 닫기

            // 어댑터에 데이터 설정 및 데이터셋 변경 알림
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }

        return recordCount; // 레코드 수 반환
    }
}
