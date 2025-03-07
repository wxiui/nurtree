package com.csstj.nurtree.ui.question;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.csstj.nurtree.MainActivity;
import com.csstj.nurtree.R;
import com.csstj.nurtree.common.AppCodeManager;
import com.csstj.nurtree.common.ListConversionExample;
import com.csstj.nurtree.common.consts.AppConst;
import com.csstj.nurtree.common.helper.DatabaseHelper;
import com.csstj.nurtree.common.util.ColorUtil;
import com.csstj.nurtree.common.util.OkHttpUtils;
import com.csstj.nurtree.data.HttpResponse;
import com.csstj.nurtree.data.TableDataInfo;
import com.csstj.nurtree.data.model.ExamInfo;
import com.csstj.nurtree.ui.login.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ExamSettingActivity extends AppCompatActivity {

    private TextView examSettingSubmit, correctScore, errorScore;
    private EditText bonusMinutes;

    private Gson gson;
    private ExamInfo examInfo = new ExamInfo();

    private AppCodeManager appCodeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_setting);

        // 隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // 设置状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().setStatusBarColor(Color.TRANSPARENT); // 状态栏透明
            getWindow().setStatusBarColor(ColorUtil.hex2Int("#50000000"));
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }

        gson = new Gson();
        appCodeManager = new AppCodeManager(getApplicationContext());

        bonusMinutes = findViewById(R.id.et_bonus_minutes);
        correctScore = findViewById(R.id.et_correct_score);
        errorScore = findViewById(R.id.et_error_score);

        // 获取传递的数据
        Intent intent = getIntent();
        if (intent != null) {

            String examInfoJson = intent.getStringExtra("examInfo");

            Log.d("QuestionActivity","examInfo:"+examInfoJson);
            examInfo = gson.fromJson(examInfoJson, ExamInfo.class);
        }

        correctScore.setText(examInfo.getResourceName());
        errorScore.setText(examInfo.getQuestionNums()+"");
        // 查询数据
        bonusMinutes.setText(examInfo.getBonusMins()+"");


        examSettingSubmit = findViewById(R.id.exam_setting_submit);
        examSettingSubmit.setOnClickListener(new MyButtonListener());
    }

    class MyButtonListener implements View.OnClickListener
    {
        public void onClick(View view)
        {
            String bonMinutes = bonusMinutes.getText().toString();
            saveAppUserResource(Integer.parseInt(bonMinutes));
        }
    }

    private void saveAppUserResource(int bonusMins){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userName", appCodeManager.getUsername());
        jsonObject.addProperty("resourceId", examInfo.getResourceId());
        jsonObject.addProperty("bonusMins", bonusMins);
        // 设置MediaType
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // 创建RequestBody对象
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);

        //创建请求
        Request request = new Request.Builder()
                .url(AppConst.SAVE_RES_INFO)
                .post(requestBody)
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
                    HttpResponse httpResponse = gson.fromJson(response.body().string(),HttpResponse.class);
                    if(httpResponse.getCode()==401){
                        // 跳转到登录页面
                        Intent intent = new Intent(ExamSettingActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else if(httpResponse.getCode()==200){
                        runOnUiThread(() -> {
                            Toast.makeText(ExamSettingActivity.this, "Successfully modified", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ExamSettingActivity.this, MainActivity.class);
                            startActivity(intent);
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
}
