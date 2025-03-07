package com.csstj.nurtree.ui.learn;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csstj.nurtree.adapter.ExamPagerAdapter;
import com.csstj.nurtree.common.ListConversionExample;
import com.csstj.nurtree.common.consts.AppConst;
import com.csstj.nurtree.common.helper.DatabaseHelper;
import com.csstj.nurtree.common.util.OkHttpUtils;
import com.csstj.nurtree.data.HttpResponse;
import com.csstj.nurtree.data.TableDataInfo;
import com.csstj.nurtree.data.model.ExamInfo;
import com.csstj.nurtree.databinding.FragmentLearnBinding;
import com.csstj.nurtree.ui.login.LoginActivity;
import com.csstj.nurtree.ui.question.ExamSettingActivity;
import com.csstj.nurtree.ui.question.QuestionActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LearnFragment extends Fragment implements ExamPagerAdapter.OnItemClickListener {

    private FragmentLearnBinding binding;
    private RecyclerView examListView;
    private ExamPagerAdapter adapter;
    private List<ExamInfo> dataList;

    private DatabaseHelper dbHelper;
    private Map<String, String> retrievedMap;

    private Gson gson;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLearnBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        gson = new Gson();
        dataList = new ArrayList<>();

        dbHelper = new DatabaseHelper(getContext());
        examListView = binding.examListView;
        examListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        getOnlineExamInfos();
        adapter = new ExamPagerAdapter(dataList, this);
        examListView.setAdapter(adapter);
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


        // 查询数据
        retrievedMap = dbHelper.retrieveMap();
        if(retrievedMap.isEmpty()){
            retrievedMap = new HashMap<>();
        }
        for(int i=0;i<=9;i++){
            String value = retrievedMap.get(i+"");
            if(value==null){
                retrievedMap.put(i+"", "10");
            }
        }
        // 插入数据
        dbHelper.insertMap(retrievedMap);



        retrievedMap = dbHelper.retrieveMap();
        List<ExamInfo> examInfos = new ArrayList<>();
        for(int i=0;i<=9;i++){
            ExamInfo examInfo = new ExamInfo();
            examInfo.setResourceName("Test"+(i+1));
            examInfo.setQuestionNums(3);
            examInfo.setBonusMins(Integer.parseInt(retrievedMap.get(i+"")));
            examInfos.add(examInfo);
        }
        return examInfos;
    }

    @Override
    public void onItemClick(int position) {
        ExamInfo examInfo = dataList.get(position);
        if(!AppConst.ISADMIN){
            // 跳转到另一个 Activity
            Intent intent = new Intent(getActivity(), QuestionActivity.class);
            // 传递数据（可选）
            intent.putExtra("examInfo", gson.toJson(examInfo));
            startActivity(intent);
        }else {
            // 跳转到另一个 Activity
            Intent intent = new Intent(getActivity(), ExamSettingActivity.class);
            // 传递数据（可选）
            intent.putExtra("examInfo", gson.toJson(examInfo));
            startActivity(intent);
        }
    }
}