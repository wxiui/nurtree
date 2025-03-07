package com.csstj.nurtree.ui.home;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csstj.nurtree.R;
import com.csstj.nurtree.adapter.ExamAdapter;
import com.csstj.nurtree.common.ListConversionExample;
import com.csstj.nurtree.common.TimeManager;
import com.csstj.nurtree.common.consts.AppConst;
import com.csstj.nurtree.common.helper.AppMonitorHelper;
import com.csstj.nurtree.common.util.OkHttpUtils;
import com.csstj.nurtree.data.TableDataInfo;
import com.csstj.nurtree.data.model.ExamInfo;
import com.csstj.nurtree.data.model.MinCircleInfo;
import com.csstj.nurtree.databinding.FragmentHomeBinding;
import com.csstj.nurtree.ui.login.LoginActivity;
import com.csstj.nurtree.ui.question.QuestionActivity;
import com.csstj.nurtree.view.CircleCountdownView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment implements ExamAdapter.OnItemClickListener {

    private FragmentHomeBinding binding;
    private CircleCountdownView circleCountdownView;

    private Handler handler = new Handler();
    private int progress = 0;

    private TimeManager timeManager;
//    private AppMonitorHelper appMonitorHelper;
    private int workerNum = 0;

    private RecyclerView examListView;
    private ExamAdapter adapter;
    private List<ExamInfo> dataList;

    private Gson gson;

    private AppMonitorHelper appMonitorHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        gson = new Gson();
        dataList = new ArrayList<>();
        circleCountdownView = binding.circleCountdownView;
        timeManager = new TimeManager(getActivity());

        if(!AppConst.ISADMIN){
            appMonitorHelper = new AppMonitorHelper(getActivity());
            // 检查并请求 PACKAGE_USAGE_STATS 权限

            // 模拟动态更新进度和文本
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    workerNum++;
                    if(workerNum==1){
                        long todayUsageMins = appMonitorHelper.getTodayUsageMins();
                        MinCircleInfo minCircleInfo = timeManager.getUsageInfo((int) todayUsageMins);
                        progress = minCircleInfo.getProgress();
                        if (progress <= 100) {
                            int minlist = minCircleInfo.getMinlist();
                            circleCountdownView.setProgress(progress);
                            circleCountdownView.setMins(minlist);
                            if(minlist>0){
                                timeManager.unblockBanApp();
                            }
                        }
                    }else if(workerNum == 5){
                        workerNum = 0;
                    }
                    handler.postDelayed(this, 3000); // 每100ms更新一次
                }
            }, 100);
        }else {
            circleCountdownView.setProgress(100);
            circleCountdownView.setMins(100);
        }


        examListView = binding.examListView;
        examListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        getOnlineExamInfos();
        //dataList = getExamInfos();
        adapter = new ExamAdapter(dataList, this);
        examListView.setAdapter(adapter);


        // 获取根布局
        ConstraintLayout rootLayout = root.findViewById(R.id.root_layout); // 假设你在布局文件中给ConstraintLayout设置了一个ID

        // 设置布局参数为MATCH_PARENT（这一步通常是可选的，因为已经在布局文件中设置了）
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        rootLayout.setLayoutParams(layoutParams);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getOnlineExamInfos(){
        //创建请求
        String url = AppConst.GET_RES_LIST;
        if(AppConst.ISADMIN){
            url = AppConst.GET_ADMIN_RES_LIST;
        }
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
                    TableDataInfo dataInfo = gson.fromJson(response.body().string(),TableDataInfo.class);
                    if(dataInfo.getCode()==401){
                        // 跳转到登录页面
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else if(dataInfo.getCode()==200){
                        requireActivity().runOnUiThread(() -> {
                            dataList.addAll(ListConversionExample.convertList(dataInfo.getRows()));
                            adapter.notifyDataSetChanged();
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

    public List<ExamInfo> getExamInfos(){
        List<ExamInfo> examInfos = new ArrayList<>();
        for(int i=0;i<=3;i++){
            ExamInfo examInfo = new ExamInfo();
            examInfo.setResourceName("Test"+(i+1));
            examInfo.setQuestionNums(3);
            examInfo.setBonusMins(10);
            examInfo.setResourceType("1");
            examInfos.add(examInfo);
        }
        return examInfos;
    }

    @Override
    public void onItemClick(int position) {
        // 跳转到另一个 Activity
        if(!AppConst.ISADMIN){
            ExamInfo examInfo = dataList.get(position);
            Intent intent = new Intent(getActivity(), QuestionActivity.class);
            // 传递数据（可选）
            intent.putExtra("examInfo", gson.toJson(examInfo));
            startActivity(intent);
        }
    }
}