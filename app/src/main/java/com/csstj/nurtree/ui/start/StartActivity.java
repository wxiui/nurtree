package com.csstj.nurtree.ui.start;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.csstj.nurtree.MainActivity;
import com.csstj.nurtree.R;
import com.csstj.nurtree.common.AppCodeGenerator;
import com.csstj.nurtree.common.AppCodeManager;


public class StartActivity extends AppCompatActivity {

    private AppCodeManager appCodeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        appCodeManager = new AppCodeManager(this);
        genAppCode();

        initView();
    }

    private void initView() {
        SharedPreferences sp=getSharedPreferences("name",MODE_PRIVATE);
        boolean is=sp.getBoolean("ok",true);
        //判断是否为第一次打开软件
        if (is){
            SharedPreferences.Editor editor=sp.edit();
            editor.putBoolean("ok",false);
            editor.apply();
            //跳转到引导页
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            finish();
        }else {
            //直接进去首页
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }

    }

    private void genAppCode(){
        // 检查是否已经生成过唯一码
        String appCode = appCodeManager.getAppCode();
        if (appCode == null) {
            // 生成唯一码并保存
            appCode = AppCodeGenerator.generateAppCode();
            appCodeManager.saveAppCode(appCode);
        }
        // 打印唯一码（可选）
        System.out.println("App Code: " + appCode);
    }

}