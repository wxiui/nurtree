package com.csstj.nurtree.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.csstj.nurtree.R;
import com.csstj.nurtree.data.model.ExamInfo;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {

    private List<ExamInfo> examInfos;
    private OnItemClickListener listener;

    // 定义点击事件接口
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // 构造函数，传入数据列表和点击事件监听器
    public ExamAdapter(List<ExamInfo> data, OnItemClickListener listener) {
        this.examInfos = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ExamInfo examInfo = examInfos.get(position);
        int img = R.drawable.ic_options_grey;
        if(examInfo.getResourceType()=="1"){
            img = R.drawable.ic_video_grey;
        }else if(examInfo.getResourceType()=="2"){
            img = R.drawable.article_grey;
        }
        holder.examImg.setImageResource(img);
        holder.resourceName.setText(examInfo.getResourceName());
        holder.examRight.setImageResource(R.drawable.right_grey);
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
        RelativeLayout exam;
        ImageView examImg;
        TextView resourceName;
        ImageView examRight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exam = itemView.findViewById(R.id.exam);
            examImg = itemView.findViewById(R.id.exam_img);
            resourceName = itemView.findViewById(R.id.exam_name);
            examRight = itemView.findViewById(R.id.exam_right);
        }
    }
}