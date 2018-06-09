package cn.njmeter.constantflowvalve.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import cn.njmeter.constantflowvalve.ConstantFlowValveApplication;
import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.bean.ClientUser;
import cn.njmeter.constantflowvalve.utils.ActivityController;
import cn.njmeter.constantflowvalve.utils.StatusBarUtil;
import cn.njmeter.constantflowvalve.widget.MyToolbar;

/**
 * 绑定第三方账号（社交账号）
 * Created by Li Yuliang on 2018/03/03.
 *
 * @author LiYuliang
 * @version 2017/03/03
 */

public class SocialAccountsActivity extends BaseActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_accounts);
        context = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, getString(R.string.BindSocialAccount), R.drawable.back_white, onClickListener);
        ClientUser.Account account = ConstantFlowValveApplication.getInstance().getAccount();
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorOrangePrimary);
        StatusBarUtil.setColor(this, mColor);
    }

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(this);
                break;
            default:
                break;
        }
    };
}
