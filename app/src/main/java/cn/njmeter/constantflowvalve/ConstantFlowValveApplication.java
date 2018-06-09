package cn.njmeter.constantflowvalve;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.njmeter.constantflowvalve.bean.ClientUser;
import cn.njmeter.constantflowvalve.sqlite.DbHelper;
import cn.njmeter.constantflowvalve.utils.CrashHandler;
import cn.njmeter.constantflowvalve.utils.GsonUtils;
import cn.njmeter.constantflowvalve.utils.LogUtils;
import cn.njmeter.constantflowvalve.utils.MyLifecycleHandler;
import cn.njmeter.constantflowvalve.utils.SharedPreferencesUtils;

/**
 * Application类
 * Created by Li Yuliang on 2017/03/01.
 *
 * @author LiYuliang
 * @version 2018/02/24
 */

public class ConstantFlowValveApplication extends MultiDexApplication {

    private static ConstantFlowValveApplication instance;
    private Context mContext;
    /**
     * 全局数据库操作类
     */
    private DbHelper mDbHelper;
    private ClientUser.Account account;
    public List<ClientUser.Server> serverList;
    private ClientUser.Version version;
    private ClientUser.Version2 version2;
    public static boolean loginSuccess;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("StartTime", "Application启动");
        mContext = getApplicationContext();

        instance = this;
        loginSuccess = false;
        // 捕捉异常
        CrashHandler.getInstance().init(this);
        //初始化SharedPreferences工具
        SharedPreferencesUtils.init(this);
        //注册Activity生命周期回调
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
        serverList = new ArrayList<>();
        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
        LogUtils.d("StartTime", "Application启动完毕");
    }

    /**
     * 单例模式中获取唯一的MyApplication实例
     *
     * @return application实例
     */
    public static ConstantFlowValveApplication getInstance() {
        if (instance == null) {
            instance = new ConstantFlowValveApplication();
        }
        return instance;
    }

    public ClientUser.Account getAccount() {
        if (account != null) {
            return account;
        } else if (loginSuccess) {
            String account = (String) SharedPreferencesUtils.getInstance().getData("account", "");
            if (TextUtils.isEmpty(account)) {
                return null;
            } else {
                ClientUser clientUser = GsonUtils.parseJSON(account, ClientUser.class);
                return clientUser.getAccount();
            }
        } else {
            return null;
        }
    }

    public ClientUser.Version getVersion() {
        if (version != null) {
            return version;
        } else if (loginSuccess) {
            String account = (String) SharedPreferencesUtils.getInstance().getData("account", "");
            if (TextUtils.isEmpty(account)) {
                return null;
            } else {
                ClientUser clientUser = GsonUtils.parseJSON(account, ClientUser.class);
                return clientUser.getVersion();
            }
        } else {
            return null;
        }
    }

    public ClientUser.Version2 getVersion2() {
        if (version2 != null) {
            return version2;
        } else if (loginSuccess) {
            String account = (String) SharedPreferencesUtils.getInstance().getData("account", "");
            if (TextUtils.isEmpty(account)) {
                return null;
            } else {
                ClientUser clientUser = GsonUtils.parseJSON(account, ClientUser.class);
                return clientUser.getVersion2();
            }
        } else {
            return null;
        }
    }

    public void setAccount(ClientUser.Account account) {
        this.account = account;
    }

    public void setVersion(ClientUser.Version version) {
        this.version = version;
    }

    public void setVersion2(ClientUser.Version2 version2) {
        this.version2 = version2;
    }

    public DbHelper getmDbHelper() {
        if (mDbHelper == null) {
            mDbHelper = new DbHelper(mContext);
            mDbHelper.getDBHelper();
            mDbHelper.open();
        }
        return mDbHelper;
    }

    public void setmDbHelper(DbHelper dbHelper) {
        mDbHelper = dbHelper;
    }
}
