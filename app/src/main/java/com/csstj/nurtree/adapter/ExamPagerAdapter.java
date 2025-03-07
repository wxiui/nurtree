package com.csstj.nurtree.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.csstj.nurtree.R;
import com.csstj.nurtree.data.model.ExamInfo;
import com.csstj.nurtree.view.CircleNumberView;

import java.util.List;

public class ExamPagerAdapter extends RecyclerView.Adapter<ExamPagerAdapter.ViewHolder> {

    private List<ExamInfo> examInfos;
    private OnItemClickListener listener;

    // 定义点击事件接口
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // 构造函数，传入数据列表和点击事件监听器
    public ExamPagerAdapter(List<ExamInfo> data, OnItemClickListener listener) {
        this.examInfos = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exampaper_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ExamInfo examInfo = examInfos.get(position);
        holder.circleNumberView.setNumber(position+1);
        holder.resourceName.setText(examInfo.getResourceName());
        holder.questionNums.setText(examInfo.getQuestionNums()+" Question");
        holder.bonusMins.setText(examInfo.getBonusMins()+" Mins");
        // 设置点击事件
        holder.exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理点击事件
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return examInfos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout exam;
        CircleNumberView circleNumberView;
        TextView resourceName;
        TextView questionNums;
        TextView bonusMins;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exam = itemView.findViewById(R.id.exam);
            circleNumberView = itemView.findViewById(R.id.circle_number_view);
            resourceName = itemView.findViewById(R.id.exam_name);
            questionNums = itemView.findViewById(R.id.question_num);
            bonusMins = itemView.findViewById(R.id.award_time);
        }
    }
}