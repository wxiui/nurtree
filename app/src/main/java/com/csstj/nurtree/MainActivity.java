package com.csstj.nurtree;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.csstj.nurtree.common.consts.AppConst;
import com.csstj.nurtree.common.receiver.MyDeviceAdminReceiver;
import com.csstj.nurtree.common.util.ColorUtil;
import com.csstj.nurtree.service.AppForegroundDetectionService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.csstj.nurtree.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ActivityResultLauncher<Intent> systemAlertWindowLauncher;
    private ActivityResultLauncher<Intent> packageUsageStatsLauncher;
    private ActivityResultLauncher<Intent> deviceAdminLauncher;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;

    private String permission_granted = "权限已授予";
    private String permission_denied = "权限被拒绝";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //设置状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().setStatusBarColor(Color.TRANSPARENT); // 状态栏透明
            getWindow().setStatusBarColor(ColorUtil.hex2Int("#50000000"));
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }

        // 注册 SYSTEM_ALERT_WINDOW 权限请求的 ActivityResultLauncher
        systemAlertWindowLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (Settings.canDrawOverlays(MainActivity.this)) {
                                Toast.makeText(MainActivity.this, "SYSTEM_ALERT_WINDOW "+permission_granted, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "SYSTEM_ALERT_WINDOW "+permission_denied, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        // 注册 PACKAGE_USAGE_STATS 权限请求的 ActivityResultLauncher
        packageUsageStatsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (Settings.ACTION_USAGE_ACCESS_SETTINGS.equals(result.getData() != null ? result.getData().toString() : null)) {
                                if (hasUsageStatsPermission()) {
                                    Toast.makeText(MainActivity.this, "PACKAGE_USAGE_STATS "+permission_granted, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "PACKAGE_USAGE_STATS "+permission_denied, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });

        // 注册 BIND_DEVICE_ADMIN 权限请求的 ActivityResultLauncher
        deviceAdminLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Toast.makeText(MainActivity.this, "BIND_DEVICE_ADMIN "+permission_granted, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "BIND_DEVICE_ADMIN "+permission_denied, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_device, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if(!AppConst.ISADMIN){
            // 检查并请求权限
            checkAndRequestPermissions();

            Intent serviceIntent = new Intent(this, AppForegroundDetectionService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    private void checkAndRequestPermissions() {
        // 检查并请求 SYSTEM_ALERT_WINDOW 权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            systemAlertWindowLauncher.launch(intent);
        }

        // 检查并请求 PACKAGE_USAGE_STATS 权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !hasUsageStatsPermission()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            packageUsageStatsLauncher.launch(intent);
        }

        // 检查并请求 BIND_DEVICE_ADMIN 权限
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
        if (!devicePolicyManager.isAdminActive(componentName)) {
            // 请求设备管理员权限
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "需要设备管理员权限以执行某些操作");
            deviceAdminLauncher.launch(intent);
        }
    }

    private boolean hasUsageStatsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.app.usage.UsageStatsManager usageStatsManager = (android.app.usage.UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            java.util.List<android.app.usage.UsageStats> stats = usageStatsManager.queryUsageStats(android.app.usage.UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            return stats != null && !stats.isEmpty();
        }
        return false;
    }

}