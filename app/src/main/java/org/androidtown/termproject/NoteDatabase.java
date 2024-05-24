package org.androidtown.termproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// NoteDatabase 클래스 정의
public class NoteDatabase {
    private static final String TAG = "NoteDatabase";  // 로그 태그 정의

    private static NoteDatabase database;  // 싱글톤 인스턴스를 위한 정적 변수
    public static String DATABASE_NAME = "todo.db";  // 데이터베이스 이름
    public static String TABLE_NOTE = "NOTE";  // 테이블 이름
    public static int DATABASE_VERSION = 1;  // 데이터베이스 버전

    private Context context;  // 컨텍스트 참조
    private SQLiteDatabase db;  // SQLite 데이터베이스 참조
    private DatabaseHelper dbHelper;  // SQLiteOpenHelper를 상속받은 헬퍼 클래스 참조

    // NoteDatabase 생성자 (프라이빗 접근제어자로 외부에서 직접 호출 불가)
    private NoteDatabase(Context context){
        this.context = context;
    }

    // NoteDatabase 인스턴스를 반환하는 정적 메서드 (싱글톤 패턴 구현)
    public static NoteDatabase getInstance(Context context){
        if(database == null){
            database = new NoteDatabase(context);
        }
        return database;
    }

    // rawQuery 메서드: SQL 쿼리를 실행하고 Cursor 반환
    public Cursor rawQuery(String SQL){
        Cursor c1 = null;
        try{
            c1 = db.rawQuery(SQL, null);
        } catch (Exception ex){
            Log.e(TAG, "Exception in rawQuery", ex);
        }
        return c1;
    }

    // execSQL 메서드: SQL 명령을 실행
    public boolean execSQL(String SQL) {
        try {
            Log.d(TAG, "SQL : " + SQL);
            db.execSQL(SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in execSQL", ex);
            return false;
        }
        return true;
    }

    // DatabaseHelper 클래스: SQLiteOpenHelper를 상속받아 데이터베이스 생성 및 관리를 담당
    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // 기존 테이블을 삭제하는 SQL 구문
            String DROP_SQL = "drop table if exists " + TABLE_NOTE;

            try {
                db.execSQL(DROP_SQL);
            } catch (Exception ex){
                Log.e(TAG, "Exception in DROP_SQL", ex);
            }

            // 새 테이블을 생성하는 SQL 구문
            String CREATE_SQL = "create table " + TABLE_NOTE + "("
                    + " _id integer NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + " TODO TEXT DEFAULT '' "
                    + ")";
            try{
                db.execSQL(CREATE_SQL);
            } catch (Exception ex){
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

            // 테이블 인덱스를 생성하는 SQL 구문
            String CREATE_INDEX_SQL = "create index " + TABLE_NOTE + "_IDX ON " + TABLE_NOTE + "("
                    + "_id"
                    + ")";
            try{
                db.execSQL(CREATE_INDEX_SQL);
            } catch (Exception ex){
                Log.e(TAG, "Exception in CREATE_INDEX_SQL", ex);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // 데이터베이스 업그레이드 시 호출되는 메서드 (현재는 비어 있음)
        }
    }

    // 데이터베이스를 여는 메서드
    public boolean open(){
        dbHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = dbHelper.getWritableDatabase();
        return true;
    }

    // 데이터베이스를 닫는 메서드
    public void close(){
        db.close();
        database = null;
    }
}
