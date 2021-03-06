package cn.njmeter.constantflowvalve.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.utils.ActivityController;
import cn.njmeter.constantflowvalve.utils.SharedPreferencesUtils;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        String userName = (String) SharedPreferencesUtils.getInstance().getData("userName_main", "");
        String passWord = (String) SharedPreferencesUtils.getInstance().getData("passWord_main", "");
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(passWord)) {
            openActivity(MainActivity.class);
            ActivityController.finishActivity(this);
        }else{
            openActivity(LoginRegisterActivity.class);
            ActivityController.finishActivity(this);
        }
    }
}
