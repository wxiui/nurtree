package com.csstj.nurtree.ui.register;

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
import com.csstj.nurtree.data.HttpResponse;
import com.csstj.nurtree.service.LoginService;
import com.csstj.nurtree.service.RegisterService;
import com.csstj.nurtree.ui.login.LoginActivity;
import com.google.gson.Gson;

public class RegisterActivity extends AppCompatActivity {

    private AppCodeManager loginManager;

    @SuppressLint("WrongViewCast")
    EditText phone, code, childAccount, childPassword;
    Button buttonRegister;
    TextView tvC,buttonBack;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //账号密码管理
        loginManager = new AppCodeManager(this);

        initView();

        buttonRegister.setOnClickListener(new MyButtonListener());
    }
    @SuppressLint("WrongViewCast")
    private void initView ()
    {
        phone = findViewById(R.id.register_phone);
        code = findViewById(R.id.register_code);
        childAccount = findViewById(R.id.register_child_account);
        childPassword = findViewById(R.id.register_child_password);

        tvC = findViewById(R.id.tvRC);
        buttonRegister = findViewById(R.id.btn_register);
        //点击注册跳转
        buttonBack = findViewById(R.id.btn_back);

        buttonBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    class MyButtonListener implements View.OnClickListener
    {
        public void onClick(View view)
        {
            String phone1 = phone.getText().toString();
            String code1 = code.getText().toString();
            String childAccount1 = childAccount.getText().toString();
            String childPassword1 = childPassword.getText().toString();

            // 模拟登录成功
            if (isValidReg(phone1, code1,childAccount1,childPassword1)) {
                // 保存登录信息
                // 保存登录信息
                RegisterService registerService = new RegisterService();
                registerService.register(phone1, code1, childAccount1, childPassword1, new RegisterService.RegCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Gson gson = new Gson();
                        HttpResponse httpResponse = gson.fromJson(response, HttpResponse.class);

                        runOnUiThread(() -> {
                            if (httpResponse.getCode() == 200) {
                                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                // 保存 token 或其他信息
                                loginManager.saveLoginInfo(phone1, code1, "");
                                // 跳转到首页
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration failed: " + httpResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // 登录失败，处理错误
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } else {
                Toast.makeText(RegisterActivity.this, "Registration failed. Please check the input of your account and password.", Toast.LENGTH_SHORT).show();
            }


        }
    }

    // 模拟登录验证
    private boolean isValidReg(String username, String password,String childAccount1,String childPassword1) {
        return !username.isEmpty() && !password.isEmpty()&&!childAccount1.isEmpty() && !childAccount1.isEmpty();
    }
}