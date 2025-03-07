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
import com.csstj.nurtree.data.model.ProfileInfo;

import java.util.List;

public class ProfilePagerAdapter extends RecyclerView.Adapter<ProfilePagerAdapter.ViewHolder> {

    private List<ProfileInfo> profileInfos;
    private OnItemClickListener listener;

    public ProfilePagerAdapter(List<ProfileInfo> data, OnItemClickListener listener) {
        this.profileInfos = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProfileInfo profileInfo = profileInfos.get(position);
        holder.profileText.setText(profileInfo.getProfileText());
        holder.profileIcon.setImageResource(profileInfo.getProfileIcon());
        holder.profileArrow.setImageResource(profileInfo.getProfileArrow());
        holder.profile.setOnClickListener(new View.OnClickListener() {
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
        return profileInfos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout profile;
        TextView profileText;
        ImageView profileIcon;
        ImageView profileArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            profileText = itemView.findViewById(R.id.profile_text);
            profileIcon = itemView.findViewById(R.id.profile_icon);
            profileArrow = itemView.findViewById(R.id.profile_arrow);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}