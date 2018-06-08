package cn.njmeter.constantflowvalve.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pkmmte.view.CircularImageView;

import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.utils.NotificationsUtils;
import cn.njmeter.constantflowvalve.utils.StatusBarUtil;

public class MainActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigation;
    private LinearLayout llMain, llNotification;
    private CircularImageView ivIcon, ivUserIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.navigation);
        ViewGroup.LayoutParams params = navigation.getLayoutParams();
        params.width = mWidth * 4 / 5;
        navigation.setLayoutParams(params);

        drawerLayout = findViewById(R.id.drawer_layout);
        llMain = findViewById(R.id.ll_main);
        ivIcon = findViewById(R.id.iv_icon);
        ivUserIcon = findViewById(R.id.iv_userIcon);
        ivIcon.setOnClickListener(onClickListener);

        drawerLayout.addDrawerListener(drawerListener);

        llNotification = findViewById(R.id.ll_notification);
        llNotification.setOnClickListener(onClickListener);
        showOrHideNotification();
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorBluePrimary);
        StatusBarUtil.setColorForDrawerLayout(this, findViewById(R.id.drawer_layout), mColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showOrHideNotification();
    }

    private View.OnClickListener onClickListener = (view) -> {
        switch (view.getId()) {
            case R.id.iv_icon:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
    };

    private DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            llMain.scrollTo(-(int) (navigation.getWidth() * slideOffset), 0);
        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    /**
     * 显示或隐藏“打开悬浮窗”的提示
     */
    public void showOrHideNotification() {
        if (NotificationsUtils.isNotificationEnabled(this)) {
            llNotification.setVisibility(View.GONE);
        } else {
            llNotification.setVisibility(View.VISIBLE);
        }
    }
}
