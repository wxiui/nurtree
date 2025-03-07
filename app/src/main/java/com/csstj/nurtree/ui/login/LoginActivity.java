package com.csstj.nurtree.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.csstj.nurtree.MainActivity;
import com.csstj.nurtree.R;
import com.csstj.nurtree.common.AppCodeManager;
import com.csstj.nurtree.common.consts.AppConst;
import com.csstj.nurtree.data.HttpResponse;
import com.csstj.nurtree.service.LoginService;
import com.csstj.nurtree.ui.register.RegisterActivity;
import com.google.gson.Gson;


public class LoginActivity extends AppCompatActivity {

    private AppCodeManager loginManager;

    @SuppressLint("WrongViewCast")
    EditText phone,pwd;
    Button buttonLogin;
    TextView tvC,buttonRegister;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initView();

        //账号密码管理
        loginManager = new AppCodeManager(this);

        buttonLogin.setOnClickListener(new MyButtonListener());
    }
    @SuppressLint("WrongViewCast")
    private void initView ()
    {
        phone = findViewById(R.id.phone);
        pwd = findViewById(R.id.pwd);
        tvC = findViewById(R.id.tvRC);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister= findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        if(!AppConst.ISADMIN){
            //点击注册跳转
            buttonRegister.setVisibility(View.GONE);
        }
    }
    //显示登录信息
    public void Login(View view)
    {

    }

    // 模拟登录验证
    private boolean isValidLogin(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    class MyButtonListener implements View.OnClickListener
    {
        public void onClick(View view)
        {
            String username=phone.getText().toString();
            String password=pwd.getText().toString();

            // 模拟登录成功
            if (isValidLogin(username, password)) {
                // 保存登录信息
                LoginService loginService = new LoginService();
                loginService.login(username, password, new LoginService.LoginCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Gson gson = new Gson();
                        HttpResponse httpResponse = gson.fromJson(response, HttpResponse.class);

                        runOnUiThread(() -> {
                            if (httpResponse.getCode()==200) {
                                Toast.makeText(LoginActivity.this, "Logged in successfully: " + httpResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                // 保存 token 或其他信息
                                String token = httpResponse.getToken();
                                loginManager.saveLoginInfo(username,password,token);
                                // 跳转到首页
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login failed: " + httpResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // 登录失败，处理错误
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "Login failed. Please check your account and password.", Toast.LENGTH_SHORT).show();
            }

        }
    }
}