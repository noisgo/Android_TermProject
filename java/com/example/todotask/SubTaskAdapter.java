package com.example.todotask;

import static com.example.todotask.TaskDetailsActivity.taskViewModel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.SubTaskViewHolder> {
    private List<SubTask> subTaskList;
    private OnSubTaskDeleteClickListener onSubTaskDeleteClickListener;

    // 생성자를 통해 데이터 전달
    public SubTaskAdapter(List<SubTask> subTaskList) {
        this.subTaskList = subTaskList;
    }

    // ViewHolder 객체를 생성하여 리턴
    @NonNull
    @Override
    public SubTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_task_list, parent, false);
        return new SubTaskViewHolder(itemView);
    }

    public void deleteSubTask(SubTask subTask) {

        subTaskList.remove(subTask);

        notifyDataSetChanged(); // 데이터 변경을 RecyclerView에 알려 UI를 업데이트합니다.
    }


    @Override
    public void onBindViewHolder(@NonNull SubTaskViewHolder holder, int position) {
        SubTask subTask = subTaskList.get(position);
        holder.textViewSubtaskContents.setText(subTask.getContents());

        holder.checkBox.setChecked(subTask.isChecked());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 체크 상태 변경 처리
                subTask.setChecked(isChecked);
                // 데이터베이스에 변경 사항 저장하는 코드 작성
            }
        });

        // 삭제 버튼 클릭 리스너 설정
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Runnable r=new Runnable() {
                    @Override
                    public void run() {
                        taskViewModel.deleteSubTask(subTask);

                    }
                };
                Thread t=new Thread(r);
                t.start();

            }
        });
    }



    // 전체 데이터의 갯수를 리턴
    @Override
    public int getItemCount() {
        return subTaskList.size();
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTaskList = subTasks;
        notifyDataSetChanged();
    }


    public interface OnSubTaskDeleteClickListener {
        void onSubTaskDeleteClick(SubTask subTask);
    }

    static class SubTaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSubtaskContents;
        CheckBox checkBox;
        ImageView deleteImageView;

        public SubTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSubtaskContents = itemView.findViewById(R.id.edit_text_subtask_contents);
            checkBox = itemView.findViewById(R.id.checkbox_subtask);
            deleteImageView = itemView.findViewById(R.id.image_view_delete_subtask);
        }
    }
}
