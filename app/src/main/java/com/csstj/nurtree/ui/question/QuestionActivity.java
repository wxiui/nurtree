package com.csstj.nurtree.ui.question;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csstj.nurtree.MainActivity;
import com.csstj.nurtree.R;
import com.csstj.nurtree.adapter.QuestionAdapter;
import com.csstj.nurtree.common.ListConversionExample;
import com.csstj.nurtree.common.TimeManager;
import com.csstj.nurtree.common.consts.AppConst;
import com.csstj.nurtree.common.helper.DatabaseHelper;
import com.csstj.nurtree.common.util.ColorUtil;
import com.csstj.nurtree.common.util.OkHttpUtils;
import com.csstj.nurtree.common.util.StatusBarUtils;
import com.csstj.nurtree.data.TableDataInfo;
import com.csstj.nurtree.data.model.ExamInfo;
import com.csstj.nurtree.data.model.Question;
import com.csstj.nurtree.ui.login.LoginActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class QuestionActivity extends AppCompatActivity {

    private RecyclerView questionListView;
    private Button nextButton;
    private QuestionAdapter questionAdapter;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private TextView questionNo;

    private TimeManager timeManager;

    private DatabaseHelper dbHelper;
    private Map<String, String> retrievedMap;
    private Gson gson;
    private ExamInfo examInfo = new ExamInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        gson = new Gson();
        questions = new ArrayList<>();
        timeManager = new TimeManager(this);
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

        // 获取传递的数据
        Intent intent = getIntent();
        if (intent != null) {

            String examInfoJson = intent.getStringExtra("examInfo");

            Log.d("QuestionActivity","examInfo:"+examInfoJson);
            examInfo = gson.fromJson(examInfoJson,ExamInfo.class);
        }
        questionNo = findViewById(R.id.question_no);
        questionListView = findViewById(R.id.question_list_view);
        nextButton = findViewById(R.id.nextButton);

        dbHelper = new DatabaseHelper(this);
        // 查询数据
        retrievedMap = dbHelper.retrieveMap();

        // Initialize questions
        //initializeQuestions();

        int questionNums = questions.size();
        // Set up RecyclerView
        questionListView.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new QuestionAdapter(questions, currentQuestionIndex);
        questionListView.setAdapter(questionAdapter);
        initializeOnlineQuestions();

        questionNo.setText(getQuestionNo(currentQuestionIndex,questionNums));
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the current question is answered
                if (questionAdapter.getSelectedAnswers().get(currentQuestionIndex) == null) {
                    Toast.makeText(QuestionActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Move to the next question
                if (currentQuestionIndex < questions.size() - 1) {
                    currentQuestionIndex++;
                    questionAdapter.setHiddenQuestionTip(true);
                    questionNo.setText(getQuestionNo(currentQuestionIndex,questionNums));
                    questionAdapter.setCurrentQuestionIndex(currentQuestionIndex);
                } else {
                    // Quiz finished
                    int score = calculateScore();
                    int bonusMins = examInfo.getBonusMins();
                    timeManager.saveBonusMins(bonusMins);
                    Toast.makeText(QuestionActivity.this, "Quiz Finished! Your Score: " + score, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(QuestionActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    private String getQuestionNo(int currentQuestionIndex, int questionNums){
        return "No. "+(currentQuestionIndex+1)+"/"+questionNums;
    }

    private void initializeOnlineQuestions() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(AppConst.GET_QUES_LIST).newBuilder();
        urlBuilder.addQueryParameter("resourceId", examInfo.getResourceId()+"");
        String url = urlBuilder.build().toString();
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
                    TableDataInfo dataInfo = gson.fromJson(responseBody,TableDataInfo.class);
                    if(dataInfo.getCode()==401){
                        // 跳转到登录页面
                        Intent intent = new Intent(QuestionActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else if(dataInfo.getCode()==200){
                        runOnUiThread(() -> {
                            currentQuestionIndex = 0;
                            questions.addAll(ListConversionExample.convertQuestionList(dataInfo.getRows()));
                            questionAdapter.setHiddenQuestionTip(true);
                            questionNo.setText(getQuestionNo(currentQuestionIndex,questions.size()));
                            questionAdapter.setSelectedAnswers(questions.size());
                            questionAdapter.setCurrentQuestionIndex(currentQuestionIndex);
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

    private void initializeQuestions() {
//        questions = new ArrayList<>();
//        questions.add(new Question("What is the capital of France?",
//                new String[]{"Paris", "London", "Berlin", "Madrid"}, "Paris",""));
//        questions.add(new Question("Which planet is known as the Red Planet?",
//                new String[]{"Earth", "Mars", "Jupiter", "Saturn"}, "Mars",""));
//        questions.add(new Question("Who wrote 'Romeo and Juliet'?",
//                new String[]{"William Shakespeare", "Charles Dickens", "Mark Twain", "Jane Austen"}, "William Shakespeare",""));
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questionAdapter.getSelectedAnswers().get(i).equals(questions.get(i).getCorrectAnswer())) {
                score++;
            }
        }
        return score;
    }
}