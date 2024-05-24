package org.androidtown.termproject;

// Note 클래스 정의: 데이터 모델로서 할 일(Note)의 속성을 저장
public class Note {
    // 클래스 변수 (속성) 정의
    int _id;  // 고유 식별자
    String todo;  // 할 일 내용

    // Note 클래스의 생성자: 새로운 Note 객체를 생성할 때 호출
    public Note(int _id, String todo){
        this._id = _id;  // 고유 식별자를 설정
        this.todo = todo;  // 할 일 내용을 설정
    }

    // _id 속성의 getter 메서드: _id 값을 반환
    public int get_id() {
        return _id;
    }

    // _id 속성의 setter 메서드: _id 값을 설정
    public void set_id(int _id) {
        this._id = _id;
    }

    // todo 속성의 getter 메서드: todo 값을 반환
    public String getTodo() {
        return todo;
    }

    // todo 속성의 setter 메서드: todo 값을 설정
    public void setTodo(String todo) {
        this.todo = todo;
    }
}
