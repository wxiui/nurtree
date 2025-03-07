package com.csstj.nurtree.ui.profile;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csstj.nurtree.R;
import com.csstj.nurtree.adapter.ProfilePagerAdapter;
import com.csstj.nurtree.common.AppCodeManager;
import com.csstj.nurtree.common.consts.AppConst;
import com.csstj.nurtree.common.manager.ConfigManager;
import com.csstj.nurtree.common.util.OkHttpUtils;
import com.csstj.nurtree.data.HttpResponse;
import com.csstj.nurtree.data.model.ProfileInfo;
import com.csstj.nurtree.databinding.FragmentProfileBinding;
import com.csstj.nurtree.ui.login.LoginActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileFragment extends Fragment implements ProfilePagerAdapter.OnItemClickListener {
    private FragmentProfileBinding binding;

    private RecyclerView profileListView;
    private ProfilePagerAdapter adapter;

    private AppCodeManager appCodeManager;

    private ConfigManager configManager;

    private Gson gson;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        gson = new Gson();
        appCodeManager = new AppCodeManager(getContext());
        configManager = new ConfigManager(getContext());
        final TextView textView = binding.name;

        String username = "Log In / Log On";
        if (!appCodeManager.isLoggedIn()) {
            // 未登录
        } else {
            // 已登录，显示欢迎信息
            username = appCodeManager.getUsername();
        }
        textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        textView.setText(username);


        profileListView = binding.profileListView;
        profileListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ProfilePagerAdapter(getProfileInfos(),this);
        profileListView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<ProfileInfo> getProfileInfos(){
        List<ProfileInfo> profileInfos = new ArrayList<>();
        profileInfos.add(new ProfileInfo(R.drawable.setting,"Administration of Account",R.drawable.right_grey));
        profileInfos.add(new ProfileInfo(R.drawable.setting,"Help",R.drawable.right_grey));
        profileInfos.add(new ProfileInfo(R.drawable.setting,"About US",R.drawable.right_grey));
        profileInfos.add(new ProfileInfo(R.drawable.setting,"Set Up",R.drawable.right_grey));
        return profileInfos;
    }

    private void getConfig(String configKey){
        String url = AppConst.GET_CONFIG_INFO + configKey;
        //创建请求
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 发起请求
        OkHttpUtils.sendRequest(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    HttpResponse dataInfo = gson.fromJson(responseBody,HttpResponse.class);
                    if(dataInfo.getCode()==401){
                        // 跳转到登录页面
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else if(dataInfo.getCode()==200){
                        requireActivity().runOnUiThread(() -> {
                            String configValue = dataInfo.getMsg().toString();
                            updateConfig(configKey,configValue);
                        });
                    }
                } else {
                    Log.e(TAG, "请求失败，状态码: " + response.code());
                }
                // 关闭响应体，避免资源泄漏
                response.close();
            }
        });
    }

    public void updateConfig(String configKey,String configValue){
        if("nurtree.app.whitelist".equals(configKey)){
            configManager.saveWhitelist(configValue);
        }else if("nurtree.app.blacklist".equals(configKey)){
            configManager.saveBlacklist(configValue);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (position==2){
            getConfig("nurtree.app.blacklist");
        }else if (position==3){
            getConfig("nurtree.app.whitelist");
        }
    }
}