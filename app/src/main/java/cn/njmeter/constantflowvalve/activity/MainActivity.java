package cn.njmeter.constantflowvalve.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.pkmmte.view.CircularImageView;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.njmeter.constantflowvalve.BuildConfig;
import cn.njmeter.constantflowvalve.ConstantFlowValveApplication;
import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.adapter.FilterConditionAdapter;
import cn.njmeter.constantflowvalve.adapter.ValveLastReportAdapter;
import cn.njmeter.constantflowvalve.adapter.TagAdapter;
import cn.njmeter.constantflowvalve.bean.FilterCondition;
import cn.njmeter.constantflowvalve.bean.HeatMeterLastData;
import cn.njmeter.constantflowvalve.bean.HeatMeterLastDataResult;
import cn.njmeter.constantflowvalve.bean.ValveLoginResult;
import cn.njmeter.constantflowvalve.constant.ApkInfo;
import cn.njmeter.constantflowvalve.constant.Constants;
import cn.njmeter.constantflowvalve.constant.NetWork;
import cn.njmeter.constantflowvalve.bean.ClientUser;
import cn.njmeter.constantflowvalve.constant.SmartWaterSupply;
import cn.njmeter.constantflowvalve.network.ExceptionHandle;
import cn.njmeter.constantflowvalve.network.NetClient;
import cn.njmeter.constantflowvalve.network.NetworkSubscriber;
import cn.njmeter.constantflowvalve.utils.ActivityController;
import cn.njmeter.constantflowvalve.utils.ApkUtils;
import cn.njmeter.constantflowvalve.utils.CipherUtils;
import cn.njmeter.constantflowvalve.utils.FileUtil;
import cn.njmeter.constantflowvalve.utils.GsonUtils;
import cn.njmeter.constantflowvalve.utils.LogUtils;
import cn.njmeter.constantflowvalve.utils.MathUtils;
import cn.njmeter.constantflowvalve.utils.NetworkUtil;
import cn.njmeter.constantflowvalve.utils.NotificationsUtils;
import cn.njmeter.constantflowvalve.utils.SharedPreferencesUtils;
import cn.njmeter.constantflowvalve.utils.StatusBarUtil;
import cn.njmeter.constantflowvalve.utils.ViewUtils;
import cn.njmeter.constantflowvalve.widget.ChooseFilterDialog;
import cn.njmeter.constantflowvalve.widget.ChooseMeterSizeDialog;
import cn.njmeter.constantflowvalve.widget.DownLoadDialog;
import cn.njmeter.constantflowvalve.widget.DownloadProgressBar;
import cn.njmeter.constantflowvalve.widget.MarqueeTextView;
import cn.njmeter.constantflowvalve.widget.MyProgressBar;
import cn.njmeter.constantflowvalve.widget.ReDownloadWarningDialog;
import cn.njmeter.constantflowvalve.widget.SegmentControlView;
import cn.njmeter.constantflowvalve.widget.UpgradeVersionDialog;
import cn.njmeter.constantflowvalve.widget.flowtaglayout.FlowTagLayout;
import cn.njmeter.constantflowvalve.widget.flowtaglayout.OnTagSelectListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private Context mContext;
    private String serverHost, httpPort, serviceName;
    private DrawerLayout drawerLayout;
    private NavigationView navigation;
    private LinearLayout llMain, llNotification;
    private CircularImageView ivIcon, ivUserIcon;
    private TextView tvNickName, tvCompanyName, tvPosition;
    private Button btnExit;
    private String versionType, latestVersionName, versionFileName, latestVersionMD5, latestVersionLog, apkDownloadPath;
    private int myVersionCode, latestVersionCode;
    private MyProgressBar downloadProgressBar;
    private TextView tvUpdateLog, tvCompletedSize, tvTotalSize;
    private float apkSize, completedSize;
    private ValveLoginResult loginResult;
    private SegmentControlView scvSearchMode;
    private TextView tvShowAdvancedFilter, tvSizeRange, tvCurrentHierarchy, tvMountPage;
    private Button tvExpandAllFilter;
    private MarqueeTextView tvConditions;
    private EditText etTimeRange, etCurrentPage, etFilterValue;
    private ListView lvFilter;
    private LinearLayout llAdvancedFilter;
    private List<FilterCondition> filterConditionList;
    private List<HeatMeterLastData> heatMeterLastDataList;
    private String fieldName, fieldValue;
    private ExpandableListView lvHeatMeterLast;
    private FilterConditionAdapter filterConditionAdapter;
    private ValveLastReportAdapter waterMeterLastReportAdapter;
    private int pageSize, totalPage, currentPage;
    private int itemContentFiltering, itemComparisonOperators, itemValueFiltering;
    private List<String> spinnerContentFiltering, spinnerComparisonOperators, spinnerMeterStatus, spinnerValveStatus;
    private FlowTagLayout fltComparisonOperators, fltValueFiltering;
    private TagAdapter<String> contentFilteringAdapter, comparisonOperatorsAdapter, valueFilteringAdapter;
    private final int MODIFY = 1, ADD = 2, maxDecimalDigits = 2;
    private final int SEARCH_MODE_NORMAL = 0, SEARCH_MODE_CHANGE_SEGMENT = 1, SEARCH_MODE_LAST = 2, SEARCH_MODE_NEXT = 3;
    private static final int REQUEST_CODE_CHOOSE_COMPANY = 1000, REQUEST_CODE_NOTIFICATION_SETTINGS = 1001;
    private boolean valveAccountCorrect = false;
    private boolean isAdminAccount = true;
    private LoginReceiver loginReceiver;
    private ImageView ivRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        navigation = findViewById(R.id.navigation);
        ViewGroup.LayoutParams params = navigation.getLayoutParams();
        params.width = mWidth * 4 / 5;
        navigation.setLayoutParams(params);

        ivRight = findViewById(R.id.iv_right);
        ivRight.setOnClickListener(onClickListener);

        drawerLayout = findViewById(R.id.drawer_layout);
        llMain = findViewById(R.id.ll_main);
        ivIcon = findViewById(R.id.iv_icon);
        ivUserIcon = findViewById(R.id.iv_userIcon);
        ivIcon.setOnClickListener(onClickListener);

        tvNickName = findViewById(R.id.tv_nickName);
        tvCompanyName = findViewById(R.id.tv_companyName);
        tvPosition = findViewById(R.id.tv_position);

        btnExit = findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(onClickListener);

        drawerLayout.addDrawerListener(drawerListener);

        llNotification = findViewById(R.id.ll_notification);
        llNotification.setOnClickListener(onClickListener);
        showOrHideNotification();

        (findViewById(R.id.ll_setMainAccount)).setOnClickListener(onClickListener);
        (findViewById(R.id.ll_setSubAccount)).setOnClickListener(onClickListener);
        (findViewById(R.id.ll_bindAccounts)).setOnClickListener(onClickListener);
        (findViewById(R.id.ll_update)).setOnClickListener(onClickListener);
        (findViewById(R.id.ll_version)).setOnClickListener(onClickListener);
        (findViewById(R.id.ll_share)).setOnClickListener(onClickListener);
        scvSearchMode = findViewById(R.id.scv_searchMode);
        tvSizeRange = findViewById(R.id.tv_size_range);
        tvCurrentHierarchy = findViewById(R.id.tv_current_hierarchy);
        tvMountPage = findViewById(R.id.tv_mount_page);
        etTimeRange = findViewById(R.id.et_time_range);
        etCurrentPage = findViewById(R.id.et_currentPage);
        llAdvancedFilter = findViewById(R.id.ll_advancedFilter);
        lvFilter = findViewById(R.id.lv_filter);
        filterConditionList = new ArrayList<>();
        filterConditionAdapter = new FilterConditionAdapter(mContext, filterConditionList);
        lvFilter.setAdapter(filterConditionAdapter);
        lvFilter.setOnItemClickListener(onItemClickListener);
        lvFilter.setOnItemLongClickListener(onItemLongClickListener);
        itemContentFiltering = -1;
        itemComparisonOperators = -1;
        itemValueFiltering = -1;

        spinnerContentFiltering = Arrays.asList(getResources().getStringArray(R.array.spinner_content_filtering));
        spinnerComparisonOperators = Arrays.asList(getResources().getStringArray(R.array.spinner_comparison_operators));
        spinnerMeterStatus = Arrays.asList(getResources().getStringArray(R.array.spinner_meter_status));
        spinnerValveStatus = Arrays.asList(getResources().getStringArray(R.array.spinner_valve_status));

        tvConditions = (findViewById(R.id.tv_conditions));
        tvShowAdvancedFilter = (findViewById(R.id.tv_showAdvancedFilter));
        tvShowAdvancedFilter.setOnClickListener(onClickListener);

        (findViewById(R.id.btn_search)).setOnClickListener(onClickListener);
        (findViewById(R.id.btn_addFilter)).setOnClickListener(onClickListener);
        (findViewById(R.id.btn_deleteAllFilter)).setOnClickListener(onClickListener);

        tvExpandAllFilter = (findViewById(R.id.btn_expandAllFilter));
        tvExpandAllFilter.setOnClickListener(onClickListener);

        tvCurrentHierarchy.setOnClickListener(onClickListener);
        tvSizeRange.setOnClickListener(onClickListener);
        scvSearchMode.setOnSegmentChangedListener(onSegmentChangedListener);
        etTimeRange.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String timeRange = etTimeRange.getText().toString().trim();
                etCurrentPage.setText("1");
                if (!TextUtils.isEmpty(timeRange) && Integer.valueOf(timeRange) > SmartWaterSupply.DATA_SEARCH_TIME_RANGE_MAX) {
                    charSequence = charSequence.toString().subSequence(0, charSequence.length() - 1);
                    etTimeRange.setText(charSequence);
                    //设置光标在末尾
                    etTimeRange.setSelection(charSequence.length());
                    showToast("时间范围1~31");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etCurrentPage.setText("1");
        (findViewById(R.id.iv_lastPage)).setOnClickListener(onClickListener);
        (findViewById(R.id.iv_nextPage)).setOnClickListener(onClickListener);
        lvHeatMeterLast = findViewById(R.id.lv_heat_meter_last);
        //Group点击展开
        lvHeatMeterLast.setOnGroupClickListener(onGroupClickListener);
        lvHeatMeterLast.setOnChildClickListener(onChildClickListener);
        lvHeatMeterLast.setOnItemLongClickListener(onItemLongClickListener);
        //只展开一个Group
        lvHeatMeterLast.setOnGroupExpandListener((groupPosition) -> {
            for (int i = 0; i < waterMeterLastReportAdapter.getGroupCount(); i++) {
                if (groupPosition != i) {
                    lvHeatMeterLast.collapseGroup(i);
                }
            }
        });

        //显示默认查询设置
        initDefaultData();

        //清除fieldName和fieldValue
        SharedPreferencesUtils.getInstance().clearData(getString(R.string.fieldName));
        SharedPreferencesUtils.getInstance().clearData(getString(R.string.fieldValue));

        loginReceiver = new LoginReceiver();
        registerReceiver(loginReceiver, new IntentFilter("login"));

        login();
    }

    private SegmentControlView.OnSegmentChangedListener onSegmentChangedListener = new SegmentControlView.OnSegmentChangedListener() {
        @Override
        public void onSegmentChanged(int newSelectedIndex) {
            scvSearchMode.setSelectedIndex(newSelectedIndex);
            etCurrentPage.setText("1");
        }
    };

    private ExpandableListView.OnGroupClickListener onGroupClickListener = (expandableListView, view, i, l) -> false;

    private ExpandableListView.OnChildClickListener onChildClickListener = (expandableListView, view, i, i1, l) -> false;

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (adapterView.getId()) {
                case R.id.lv_filter:
                    FilterCondition filterCondition = filterConditionList.get(i);
                    itemContentFiltering = filterCondition.getContentFilteringPosition();
                    itemComparisonOperators = filterCondition.getComparisonOperatorsPosition();
                    itemValueFiltering = filterCondition.getValueFilteringPosition();
                    showChooseItemDialog(MODIFY, i);
                    break;
                default:
                    break;
            }
        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (adapterView.getId()) {
                case R.id.lv_filter:
                    filterConditionList.remove(i);
                    filterConditionAdapter.notifyDataSetChanged();
                    if (filterConditionList.size() != 0) {
                        tvConditions.setText(String.format(getString(R.string.conditions), filterConditionList.get(0).getContentFiltering(),
                                filterConditionList.get(0).getComparisonOperators(), filterConditionList.get(0).getValueFiltering(), filterConditionList.size()));
                    } else {
                        filterConditionList.clear();
                        filterConditionAdapter.notifyDataSetChanged();
                        tvConditions.setText("");
                        etCurrentPage.setText("1");
                        changeListVisibility(false, false);
                    }
                    break;
                default:
                    break;
            }
            //避免触发点击事件
            return true;
        }
    };


    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorOrangePrimary);
        StatusBarUtil.setColorForDrawerLayout(this, findViewById(R.id.drawer_layout), mColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //显示默认查询设置
        initDefaultData();
        initPage();
        showOrHideNotification();
    }

    public class LoginReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            fieldName = "";
            fieldValue = "";
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            login();
        }
    }

    private View.OnClickListener onClickListener = (view) -> {
        ClientUser.Account account = ConstantFlowValveApplication.getInstance().getAccount();
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_right:
                if (!ConstantFlowValveApplication.loginSuccess) {
                    openActivity(LoginRegisterActivity.class);
                    return;
                }
                if (!valveAccountCorrect) {
                    showToast("请检查恒流阀账号");
                    return;
                }
                intent = new Intent(MainActivity.this, ChooseCompanyActivity.class);
                intent.putExtra(getString(R.string.waterCompanyName), GsonUtils.convertJSON(loginResult));
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_COMPANY);
                break;
            case R.id.ll_notification:
                intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
                break;
            case R.id.iv_icon:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.ll_setMainAccount:
                if (account == null) {
                    openActivity(LoginRegisterActivity.class);
                } else {
                    openActivity(SetMainAccountActivity.class);
                }
                break;
            case R.id.ll_setSubAccount:
                if (account == null) {
                    openActivity(LoginRegisterActivity.class);
                } else {
                    openActivity(SetSubAccountActivity.class);
                }
                break;
            case R.id.ll_bindAccounts:
                openActivity(SocialAccountsActivity.class);
                break;
            case R.id.ll_update:
                if (account == null) {
                    showToast("请登陆后检查版本更新");
                } else {
                    if (account.getStable_Update() == 0) {
                        showToast("已禁用所有更新");
                    } else {
                        LogUtils.d("version", "myVersionCode" + myVersionCode + ",latestVersionCode" + latestVersionCode);
                        if (myVersionCode < latestVersionCode) {
                            showDialogUpdate();
                        } else {
                            showToast("已经是最新的版本");
                        }
                    }
                }
                break;
            case R.id.ll_version:
                openActivity(VersionsActivity.class);
                break;
            case R.id.ll_share:
                showToast("分享功能暂未开放");
                break;
            case R.id.btn_exit:
                SharedPreferencesUtils.getInstance().clearData("passWord_main");
                SharedPreferencesUtils.getInstance().clearData("account");
                ConstantFlowValveApplication.loginSuccess = false;
                openActivity(LoginRegisterActivity.class);
                ActivityController.finishActivity(this);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                break;
            case R.id.btn_search:
                //查询数据
                if (checkAccount()) {
                    searchWaterMeter(SEARCH_MODE_NORMAL);
                }
                break;
            case R.id.tv_current_hierarchy:
                //层级选择
                intent = new Intent(mContext, ChooseHierarchyActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.tv_size_range:
                //修改口径范围
                showSizeRangeDialog();
                break;
            case R.id.tv_showAdvancedFilter:
                if (llAdvancedFilter.getVisibility() == View.VISIBLE) {
                    llAdvancedFilter.setVisibility(View.GONE);
                    tvShowAdvancedFilter.setText(getString(R.string.ShowAdvancedFilter));
                } else {
                    llAdvancedFilter.setVisibility(View.VISIBLE);
                    tvShowAdvancedFilter.setText(getString(R.string.HideAdvancedFilter));
                }
                break;
            case R.id.btn_addFilter:
                if (filterConditionList.size() == 5) {
                    showToast(getString(R.string.Only5FiltersCanBeAdded));
                } else {
                    showChooseItemDialog(ADD, 0);
                }
                break;
            case R.id.btn_deleteAllFilter:
                filterConditionList.clear();
                filterConditionAdapter.notifyDataSetChanged();
                tvConditions.setText("");
                etCurrentPage.setText("1");
                changeListVisibility(false, false);
                break;
            case R.id.btn_expandAllFilter:
                if (lvFilter.getVisibility() == View.VISIBLE) {
                    changeListVisibility(false, true);
                } else if (lvFilter.getVisibility() == View.GONE) {
                    changeListVisibility(true, false);
                }
                break;
            case R.id.iv_lastPage:
                //上一页
                if (currentPage > 1) {
                    currentPage--;
                    searchWaterMeter(SEARCH_MODE_LAST);
                } else {
                    showToast("已经到第一页");
                }
                etCurrentPage.clearFocus();
                break;
            case R.id.iv_nextPage:
                //下一页
                if (currentPage < totalPage) {
                    currentPage++;
                    searchWaterMeter(SEARCH_MODE_NEXT);
                } else {
                    showToast("已经到最后一页");
                }
                etCurrentPage.clearFocus();
                break;
            default:
                break;
        }
    };

    private boolean checkAccount() {
        if (!ConstantFlowValveApplication.loginSuccess) {
            openActivity(LoginRegisterActivity.class);
            return false;
        }
        if (!valveAccountCorrect) {
            showToast("请检查恒流阀账号");
            return false;
        }
        if (isAdminAccount) {
            Intent intent = new Intent(MainActivity.this, ChooseCompanyActivity.class);
            intent.putExtra(getString(R.string.waterCompanyName), GsonUtils.convertJSON(loginResult));
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_COMPANY);
            return false;
        }
        return true;
    }

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

    private void login() {
        if (ConstantFlowValveApplication.loginSuccess) {
            ClientUser.Account account = ConstantFlowValveApplication.getInstance().getAccount();
            loginConstantFlowValve(account.getUser_Name_XHS(), account.getPass_Word_XHS());
        } else {
            String userName = (String) SharedPreferencesUtils.getInstance().getData("userName_main", "");
            String passWord = (String) SharedPreferencesUtils.getInstance().getData("passWord_main", "");
            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(passWord)) {
                //如果之前登陆成功过保存了账号密码，则直接调用登录接口在后台登录
                loginMainAccount(userName, passWord);
            }
        }
    }

    /**
     * 登录方法
     *
     * @param userName 用户名
     * @param passWord 密码
     */
    private void loginMainAccount(String userName, String passWord) {
        Map<String, String> params = new HashMap<>(4);
        params.put("loginName", userName);
        params.put("password", passWord);
        params.put("versionCode", String.valueOf(ApkUtils.getVersionCode(mContext)));
        params.put("apkTypeId", ApkInfo.APK_TYPE_ID_CONSTANT_FLOW_VALVE);

        Observable<String> clientUserCall = NetClient.getInstances(NetClient.BASE_URL_PROJECT).getNjMeterApi().loginMainAccount(params);
        clientUserCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<String>(mContext, getClass().getSimpleName()) {

            @Override
            public void onStart() {
                super.onStart();
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast("当前网络不可用，请检查网络");
                    if (!isUnsubscribed()) {
                        unsubscribe();
                    }
                } else {
                    showLoadingDialog(mContext, "登陆中", true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast("" + responseThrowable.message);
            }

            @Override
            public void onNext(String s) {
                cancelDialog();
                LogUtils.d("retrofit", s);
                try {
                    CipherUtils des = new CipherUtils(NetClient.SECRET_KRY);
                    String result = des.decrypt(s);
                    ClientUser clientUser = GsonUtils.parseJSON((result), ClientUser.class);
                    String mark = clientUser.getResult();
                    String message = clientUser.getMsg();
                    LogUtils.d("retrofit", GsonUtils.convertJSON(clientUser));
                    switch (mark) {
                        case Constants.SUCCESS:
                            SharedPreferencesUtils.getInstance().saveData("account", GsonUtils.convertJSON(clientUser));
                            ConstantFlowValveApplication.loginSuccess = true;
                            LogUtils.d("登陆成功");
                            ConstantFlowValveApplication.getInstance().setAccount(clientUser.getAccount());
                            ConstantFlowValveApplication.getInstance().setVersion(clientUser.getVersion());
                            ConstantFlowValveApplication.getInstance().setVersion2(clientUser.getVersion2());
                            ConstantFlowValveApplication.getInstance().serverList = clientUser.getServer();
                            initPage();
                            checkNewVersion();
                            loginConstantFlowValve(clientUser.getAccount().getUser_Name_XHS(), clientUser.getAccount().getPass_Word_XHS());
                            break;
                        default:
                            showToast("登陆失败，" + message);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始化页面（根据是否已登录）
     */
    private void initPage() {
        if (!ConstantFlowValveApplication.loginSuccess) {
            tvNickName.setText("暂无昵称");
            tvCompanyName.setText("未登录");
            tvPosition.setText("（点击此处登录）");
            btnExit.setVisibility(View.GONE);
        } else {
            String photoPath = "http://" + NetWork.SERVER_HOST_MAIN + ":" + NetWork.SERVER_PORT_MAIN + "/" + ConstantFlowValveApplication.getInstance().getAccount().getHead_Portrait_URL().replace("\\", "/");
            // 加载头像
            Glide.with(this).load(photoPath)
                    .error(R.drawable.user_icon)
                    .placeholder(R.drawable.user_icon)
                    .dontAnimate()
                    .into(ivUserIcon);
            Glide.with(this).load(photoPath)
                    .error(R.drawable.user_icon)
                    .placeholder(R.drawable.user_icon)
                    .dontAnimate()
                    .into(ivIcon);
            SharedPreferencesUtils.getInstance().saveData("userIconPath", photoPath);
            String nickName = ConstantFlowValveApplication.getInstance().getAccount().getNickName();
            String companyName = ConstantFlowValveApplication.getInstance().getAccount().getName_Company();
            String userPosition = ConstantFlowValveApplication.getInstance().getAccount().getPosition_Company();
            tvNickName.setText(nickName.equals(Constants.EMPTY) ? "未设置昵称" : nickName);
            tvCompanyName.setText(companyName.equals(Constants.EMPTY) ? "暂无单位名称" : companyName);
            tvPosition.setText(userPosition.equals(Constants.EMPTY) ? "暂无职务" : userPosition);
            btnExit.setVisibility(View.VISIBLE);
        }
    }

    private void loginConstantFlowValve(String mUsername, String mPassword) {
        ClientUser.Account account = ConstantFlowValveApplication.getInstance().getAccount();
        serverHost = account.getServer_Host_XHS();
        httpPort = account.getHttp_Port_XHS();
        serviceName = account.getService_Name_XHS();
        Map<String, String> energyManagerParams = new HashMap<>(3);
        energyManagerParams.put("loginName", mUsername);
        energyManagerParams.put("password", mPassword);
        energyManagerParams.put("type", "heat");
        Observable<ValveLoginResult> waterMeterLoginResultObservable = NetClient.getInstances(NetClient.getBaseUrl(serverHost, httpPort, serviceName)).getNjMeterApi().loginWaterMeter(energyManagerParams);
        waterMeterLoginResultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<ValveLoginResult>(mContext, getClass().getSimpleName()) {

            @Override
            public void onStart() {
                super.onStart();
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast("当前网络不可用，请检查网络");
                    if (!isUnsubscribed()) {
                        unsubscribe();
                    }
                } else {
                    showLoadingDialog(mContext, "登陆中", true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast(responseThrowable.message);
            }

            @Override
            public void onNext(ValveLoginResult valveLoginResult) {
                cancelDialog();
                if (valveLoginResult == null) {
                    showToast("请求失败，返回值异常");
                } else {
                    String result = valveLoginResult.getResult();
                    if (result.equals(Constants.SUCCESS)) {
                        Intent intent;
                        valveAccountCorrect = true;
                        switch (valveLoginResult.getPrivilege()) {
                            case "管理员":
                                isAdminAccount = true;
                                ivRight.setVisibility(View.VISIBLE);
                                loginResult = valveLoginResult;
                                intent = new Intent(MainActivity.this, ChooseCompanyActivity.class);
                                intent.putExtra(getString(R.string.waterCompanyName), GsonUtils.convertJSON(loginResult));
                                startActivityForResult(intent, REQUEST_CODE_CHOOSE_COMPANY);
                                break;
                            case "普通":
                                //对象中拿到集合
                                isAdminAccount = false;
                                ivRight.setVisibility(View.GONE);
                                ValveLoginResult.Data data = valveLoginResult.getData().get(0);
                                //判断普通管理员的最高权限
                                int supplierId = data.getSupplierId();
                                int exchangStationId = data.getExchangStationId();
                                int villageId = data.getVillageId();
                                int buildingId = data.getBuildingId();
                                int entranceId = data.getEntranceId();
                                if (entranceId != -1) {
                                    fieldName = "entranceId";
                                    fieldValue = String.valueOf(entranceId);
                                } else if (buildingId != -1) {
                                    fieldName = "buildingId";
                                    fieldValue = String.valueOf(buildingId);
                                } else if (villageId != -1) {
                                    fieldName = "villageId";
                                    fieldValue = String.valueOf(villageId);
                                } else if (exchangStationId != -1) {
                                    fieldName = "exchangStationId";
                                    fieldValue = String.valueOf(exchangStationId);
                                } else if (supplierId != -1) {
                                    fieldName = "supplierId";
                                    fieldValue = String.valueOf(supplierId);
                                }

                                SharedPreferencesUtils.getInstance().saveData(getString(R.string.fieldName), fieldName);
                                SharedPreferencesUtils.getInstance().saveData(getString(R.string.fieldValue), fieldValue);

                                searchWaterMeter(SEARCH_MODE_NORMAL);
                                break;
                            default:
                                showToast("信息有误");
                                break;
                        }
                    } else {
                        valveAccountCorrect = false;
                        isAdminAccount = false;
                        showToast(valveLoginResult.getMsg());
                    }
                }
            }
        });
    }

    /**
     * 检查是否有新版本
     */
    private void checkNewVersion() {
        String versionUrl = "";
        myVersionCode = ApkUtils.getVersionCode(mContext);
        //正式版、预览版本更新检查
        ClientUser.Version version = ConstantFlowValveApplication.getInstance().getVersion();
        ClientUser.Version2 version2 = ConstantFlowValveApplication.getInstance().getVersion2();
        ClientUser.Account account = ConstantFlowValveApplication.getInstance().getAccount();
        //是否接收正式版更新
        int stableUpdate = account.getStable_Update();
        //是否接收预览版更新
        int betaUpdate = account.getBeta_Update();
        if (stableUpdate == 1 && betaUpdate == 1) {
            //如果接收预览版更新（前提是接收正式版更新）
            if (version2 == null) {
                //如果预览版不存在，则用正式版代替
                if (version != null) {
                    //如果正式版存在，则采用正式版的值
                    versionType = getString(R.string.versionType_stable);
                    latestVersionCode = version.getVersionCode();
                    latestVersionName = version.getVersionName();
                    versionFileName = version.getVersionFileName();
                    latestVersionMD5 = version.getMd5Value();
                    latestVersionLog = version.getVersionLog();
                    versionUrl = version.getVersionUrl();
                }
            } else {
                //如果预览版存在
                if (version == null) {
                    //如果正式版不存在，直接使用预览版的值
                    versionType = getString(R.string.versionType_preview);
                    latestVersionCode = version2.getVersionCode();
                    latestVersionName = version2.getVersionName();
                    versionFileName = version2.getVersionFileName();
                    latestVersionMD5 = version2.getMd5Value();
                    latestVersionLog = version2.getVersionLog();
                    versionUrl = version2.getVersionUrl();
                } else {
                    //如果正式版存在，比较正式版与预览版的版本号大小
                    if (version2.getVersionCode() > version.getVersionCode()) {
                        //如果预览版版本号比正式版版本号大,则使用预览版的值
                        versionType = getString(R.string.versionType_preview);
                        latestVersionCode = version2.getVersionCode();
                        latestVersionName = version2.getVersionName();
                        versionFileName = version2.getVersionFileName();
                        latestVersionMD5 = version2.getMd5Value();
                        latestVersionLog = version2.getVersionLog();
                        versionUrl = version2.getVersionUrl();
                    } else {
                        //如果正式版版本号比预览版版本号大,则使用正式版的值
                        versionType = getString(R.string.versionType_stable);
                        latestVersionCode = version.getVersionCode();
                        latestVersionName = version.getVersionName();
                        versionFileName = version.getVersionFileName();
                        latestVersionMD5 = version.getMd5Value();
                        latestVersionLog = version.getVersionLog();
                        versionUrl = version.getVersionUrl();
                    }
                }
            }
        } else if (stableUpdate == 1 && betaUpdate == 0) {
            //如果只接收正式版更新，不接收预览版更新
            if (version != null) {
                //如果正式版存在
                versionType = getString(R.string.versionType_stable);
                latestVersionCode = version.getVersionCode();
                latestVersionName = version.getVersionName();
                versionFileName = version.getVersionFileName();
                latestVersionMD5 = version.getMd5Value();
                latestVersionLog = version.getVersionLog();
                versionUrl = version.getVersionUrl();
            }
        }
        apkDownloadPath = versionUrl.replace("\\", "/");
        if (myVersionCode < latestVersionCode) {
            showDialogUpdate();
        }
    }


    /**
     * 提示版本更新的对话框
     */
    private void showDialogUpdate() {
        UpgradeVersionDialog upgradeVersionDialog = new UpgradeVersionDialog(mContext);
        upgradeVersionDialog.setCancelable(false);
        ((TextView) upgradeVersionDialog.findViewById(R.id.tv_versionLog)).setText(latestVersionLog);
        ((TextView) upgradeVersionDialog.findViewById(R.id.tv_currentVersion)).setText(ApkUtils.getVersionName(mContext));
        ((TextView) upgradeVersionDialog.findViewById(R.id.tv_latestVersion)).setText(latestVersionName);
        ((TextView) upgradeVersionDialog.findViewById(R.id.title_name)).setText(String.format(getString(R.string.update_version), latestVersionName + versionType));
        upgradeVersionDialog.setOnDialogClickListener(new UpgradeVersionDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                if (isDownloaded()) {
                    ReDownloadWarningDialog reDownloadWarningDialog = new ReDownloadWarningDialog(mContext, getString(R.string.warning_redownload));
                    reDownloadWarningDialog.setCancelable(false);
                    reDownloadWarningDialog.setOnDialogClickListener(new ReDownloadWarningDialog.OnDialogClickListener() {
                        @Override
                        public void onOKClick() {
                            //直接安装
                            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), versionFileName);
                            installApk(file);
                        }

                        @Override
                        public void onCancelClick() {
                            //下载最新的版本程序
                            downloadApk();
                        }
                    });
                    reDownloadWarningDialog.show();
                } else {
                    //下载最新的版本程序
                    downloadApk();
                }
            }

            @Override
            public void onCancelClick() {
                upgradeVersionDialog.dismiss();
            }
        });
        upgradeVersionDialog.show();
    }

    /**
     * 判断是否已经下载过该文件
     *
     * @return boolean
     */
    private boolean isDownloaded() {
        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + versionFileName);
        LogUtils.d("MD5", file.getPath());
        return file.isFile() && latestVersionMD5.equals(FileUtil.getFileMD5(file));
    }

    /**
     * 下载新版本程序
     */
    private void downloadApk() {
        if (apkDownloadPath.equals(Constants.EMPTY)) {
            showToast("下载路径有误，请联系客服");
        } else {
            DownLoadDialog progressDialog = new DownLoadDialog(mContext);
            downloadProgressBar = progressDialog.findViewById(R.id.progressbar_download);
            tvUpdateLog = progressDialog.findViewById(R.id.tv_updateLog);
            tvUpdateLog.setText(latestVersionLog);
            tvCompletedSize = progressDialog.findViewById(R.id.tv_completedSize);
            tvTotalSize = progressDialog.findViewById(R.id.tv_totalSize);
            progressDialog.setCancelable(false);
            progressDialog.show();
            NetClient.downloadFileProgress((currentBytes, contentLength, done) -> {
                //获取到文件的大小
                apkSize = MathUtils.formatFloat((float) contentLength / 1024f / 1024f, 2);
                tvTotalSize.setText(String.format(getString(R.string.file_size_m), String.valueOf(apkSize)));
                //已完成大小
                completedSize = MathUtils.formatFloat((float) currentBytes / 1024f / 1024f, 2);
                tvCompletedSize.setText(String.format(getString(R.string.file_size_m), String.valueOf(completedSize)));
                downloadProgressBar.setProgress(MathUtils.formatFloat(completedSize / apkSize * 100, 1));
                if (done) {
                    progressDialog.dismiss();
                }
            }, new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    //处理下载文件
                    if (response.body() != null) {
                        try {
                            InputStream is = response.body().byteStream();
                            //定义下载后文件的路径和名字，例如：/apk/JiangSuMetter_1.0.1.apk
                            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + versionFileName);
                            FileOutputStream fos = new FileOutputStream(file);
                            BufferedInputStream bis = new BufferedInputStream(is);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = bis.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                            bis.close();
                            is.close();
                            installApk(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast("下载出错，" + e.getMessage() + "，请联系管理员");
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    progressDialog.dismiss();
                    showToast("下载出错，" + t.getMessage() + "，请联系管理员");
                }
            });
        }
    }

    /**
     * 安装apk
     *
     * @param file 需要安装的apk
     */
    private void installApk(File file) {
        //先验证文件的正确性和完整性（通过MD5值）
        if (file.isFile() && latestVersionMD5.equals(FileUtil.getFileMD5(file))) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);//在AndroidManifest中的android:authorities值
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            startActivity(intent);
        } else {
            showToast("文件异常，无法安装");
        }
    }

    /**
     * 展开或收起高级筛选条件
     */
    private void changeListVisibility(boolean showList, boolean showText) {
        if (showList) {
            if (filterConditionList.size() != 0) {
                lvFilter.setVisibility(View.VISIBLE);
            } else {
                lvFilter.setVisibility(View.GONE);
            }
            tvExpandAllFilter.setText(R.string.CollapseAll);
        } else {
            lvFilter.setVisibility(View.GONE);
            tvExpandAllFilter.setText(R.string.ExpandAll);
        }
        if (showText) {
            if (filterConditionList.size() != 0) {
                tvConditions.setVisibility(View.VISIBLE);
            } else {
                tvConditions.setVisibility(View.GONE);
            }
        } else {
            tvConditions.setVisibility(View.GONE);
        }
    }

    /**
     * 显示设置查询口径范围的对话框
     */
    private void showSizeRangeDialog() {
        ChooseMeterSizeDialog chooseMeterSizeDialog = new ChooseMeterSizeDialog(mContext);
        chooseMeterSizeDialog.setCancelable(true);
        EditText etMeterSizeMinDialog = chooseMeterSizeDialog.findViewById(R.id.et_meterSizeMin);
        EditText etMeterSizeMaxDialog = chooseMeterSizeDialog.findViewById(R.id.et_meterSizeMax);
        etMeterSizeMinDialog.setText((String) SharedPreferencesUtils.getInstance().getData("MeterSize_Min_Default", SmartWaterSupply.DATA_SEARCH_MIN_SIZE));
        etMeterSizeMaxDialog.setText((String) SharedPreferencesUtils.getInstance().getData("MeterSize_Max_Default", SmartWaterSupply.DATA_SEARCH_MAX_SIZE));
        ViewUtils.setCharSequence(etMeterSizeMinDialog);
        ViewUtils.setCharSequence(etMeterSizeMaxDialog);
        chooseMeterSizeDialog.setOnDialogClickListener(() -> {
            String minSize = etMeterSizeMinDialog.getText().toString();
            String maxSize = etMeterSizeMaxDialog.getText().toString();
            if (TextUtils.isEmpty(minSize)) {
                showToast("请填写最小口径");
                return;
            }
            if (TextUtils.isEmpty(maxSize)) {
                showToast("请填写最大口径");
                return;
            }
            //如果最小口径大于最大口径，则自动交换两者的值
            if (Integer.valueOf(minSize) > Integer.valueOf(maxSize)) {
                String temp = minSize;
                minSize = maxSize;
                maxSize = temp;
            }
            SharedPreferencesUtils.getInstance().saveData("MeterSize_Min", minSize);
            SharedPreferencesUtils.getInstance().saveData("MeterSize_Max", maxSize);
            String searchRange = String.format(getString(R.string.range_meterSize_DN), minSize, maxSize);
            //当查询范围发生改变时，标记
            if (!searchRange.equals(tvSizeRange.getText().toString())) {
                tvSizeRange.setText(searchRange);
                etCurrentPage.setText("1");
            }
            chooseMeterSizeDialog.dismiss();
        });
        chooseMeterSizeDialog.show();
    }

    /**
     * 显示筛选条件对话框
     */
    private void showChooseItemDialog(int mode, int position) {
        switch (mode) {
            case ADD:
                initItem();
                break;
            case MODIFY:
                itemContentFiltering = filterConditionList.get(position).getContentFilteringPosition();
                itemComparisonOperators = filterConditionList.get(position).getComparisonOperatorsPosition();
                break;
            default:
                break;
        }

        ChooseFilterDialog chooseFilterDialog = new ChooseFilterDialog(mContext);
        chooseFilterDialog.setCancelable(false);
        FlowTagLayout fltContentFiltering = chooseFilterDialog.findViewById(R.id.flt_content_filtering);
        fltComparisonOperators = chooseFilterDialog.findViewById(R.id.flt_comparison_operators);
        fltValueFiltering = chooseFilterDialog.findViewById(R.id.flt_value_filtering);
        etFilterValue = chooseFilterDialog.findViewById(R.id.et_filterValue);

        contentFilteringAdapter = new TagAdapter<>(mContext);
        comparisonOperatorsAdapter = new TagAdapter<>(mContext);
        valueFilteringAdapter = new TagAdapter<>(mContext);

        //比较项
        fltContentFiltering.setAdapter(contentFilteringAdapter);
        fltContentFiltering.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        contentFilteringAdapter.onlyAddAll(spinnerContentFiltering);
        if (itemContentFiltering != -1) {
            fltContentFiltering.setSelectedOption(itemContentFiltering);
        } else {
            fltContentFiltering.clearAllOption();
        }
        //比较符
        fltComparisonOperators.setAdapter(comparisonOperatorsAdapter);
        fltComparisonOperators.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        comparisonOperatorsAdapter.onlyAddAll(spinnerComparisonOperators);
        if (itemComparisonOperators != -1) {
            fltComparisonOperators.setSelectedOption(itemComparisonOperators);
        } else {
            fltComparisonOperators.clearAllOption();
        }
        //比较值
        fltValueFiltering.setAdapter(valueFilteringAdapter);
        fltValueFiltering.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        switch (mode) {
            case ADD:
                etFilterValue.setVisibility(View.VISIBLE);
                fltValueFiltering.setVisibility(View.GONE);
                valueFilteringAdapter.onlyAddAll(spinnerMeterStatus);
                if (itemValueFiltering != -1) {
                    fltValueFiltering.setSelectedOption(itemValueFiltering);
                } else {
                    fltValueFiltering.clearAllOption();
                }
                break;
            case MODIFY:
                switch (filterConditionList.get(position).getContentFilteringPosition()) {
                    case 0:
                    case 1:
                        fltComparisonOperators.getChildAt(0).setVisibility(View.GONE);
                        fltComparisonOperators.getChildAt(2).setVisibility(View.GONE);
                        break;
                    case 2:
                    case 3:
                    case 4:
                        fltComparisonOperators.getChildAt(3).setVisibility(View.GONE);
                        break;
                    case 5:
                        valueFilteringAdapter.clearAndAddAll(spinnerMeterStatus);
                        fltComparisonOperators.getChildAt(0).setVisibility(View.GONE);
                        fltComparisonOperators.getChildAt(2).setVisibility(View.GONE);
                        fltComparisonOperators.getChildAt(3).setVisibility(View.GONE);
                        break;
                    case 6:
                        valueFilteringAdapter.clearAndAddAll(spinnerValveStatus);
                        fltComparisonOperators.getChildAt(0).setVisibility(View.GONE);
                        fltComparisonOperators.getChildAt(2).setVisibility(View.GONE);
                        fltComparisonOperators.getChildAt(3).setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
                if (filterConditionList.get(position).isHasCustomValue()) {
                    etFilterValue.setVisibility(View.VISIBLE);
                    fltValueFiltering.setVisibility(View.GONE);
                    etFilterValue.setText(filterConditionList.get(position).getValueFiltering());
                    ViewUtils.setCharSequence(etFilterValue);
                } else {
                    etFilterValue.setVisibility(View.GONE);
                    fltValueFiltering.setVisibility(View.VISIBLE);
                    itemValueFiltering = filterConditionList.get(position).getValueFilteringPosition();
                    if (itemValueFiltering != -1) {
                        fltValueFiltering.setSelectedOption(itemValueFiltering);
                    } else {
                        fltValueFiltering.clearAllOption();
                    }
                }
                break;
            default:
                break;
        }

        fltContentFiltering.setOnTagSelectListener(onTagSelectListener);
        fltComparisonOperators.setOnTagSelectListener(onTagSelectListener);
        fltValueFiltering.setOnTagSelectListener(onTagSelectListener);

        chooseFilterDialog.setOnDialogClickListener(new ChooseFilterDialog.OnDialogClickListener() {
            @Override
            public void onCancelClick() {
                initItem();
                chooseFilterDialog.dismiss();
            }

            @Override
            public void onClearClick() {
                contentFilteringAdapter.notifyDataSetChanged();
                comparisonOperatorsAdapter.notifyDataSetChanged();
                valueFilteringAdapter.notifyDataSetChanged();
                initItem();
            }

            @Override
            public void onSaveClick() {
                FilterCondition filterCondition = new FilterCondition();
                switch (itemContentFiltering) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        if (itemContentFiltering == -1 || itemComparisonOperators == -1 || TextUtils.isEmpty(etFilterValue.getText().toString().trim())) {
                            showToast("请完善筛选条件");
                            return;
                        }
                        filterCondition.setContentFiltering((String) contentFilteringAdapter.getItem(itemContentFiltering));
                        filterCondition.setComparisonOperators((String) comparisonOperatorsAdapter.getItem(itemComparisonOperators));
                        filterCondition.setValueFiltering(etFilterValue.getText().toString().trim());
                        filterCondition.setHasCustomValue(true);
                        break;
                    case 5:
                    case 6:
                        if (itemContentFiltering == -1 || itemComparisonOperators == -1 || itemValueFiltering == -1) {
                            showToast("请完善筛选条件");
                            return;
                        }
                        filterCondition.setContentFiltering((String) contentFilteringAdapter.getItem(itemContentFiltering));
                        filterCondition.setComparisonOperators((String) comparisonOperatorsAdapter.getItem(itemComparisonOperators));
                        filterCondition.setValueFiltering((String) valueFilteringAdapter.getItem(itemValueFiltering));
                        filterCondition.setHasCustomValue(false);
                        break;
                    default:
                        break;
                }
                filterCondition.setContentFilteringPosition(itemContentFiltering);
                filterCondition.setComparisonOperatorsPosition(itemComparisonOperators);
                filterCondition.setValueFilteringPosition(itemValueFiltering);
                switch (mode) {
                    case ADD:
                        filterConditionList.add(filterCondition);
                        filterConditionAdapter.notifyDataSetChanged();
                        initItem();
                        break;
                    case MODIFY:
                        filterConditionList.remove(position);
                        filterConditionList.add(position, filterCondition);
                        filterConditionAdapter.notifyDataSetChanged();
                        initItem();
                        break;
                    default:
                        break;
                }
                etCurrentPage.setText("1");
                tvConditions.setText(String.format(getString(R.string.conditions), filterConditionList.get(0).getContentFiltering(),
                        filterConditionList.get(0).getComparisonOperators(), filterConditionList.get(0).getValueFiltering(), filterConditionList.size()));
                changeListVisibility(true, false);
                chooseFilterDialog.dismiss();
            }
        });
        chooseFilterDialog.show();
    }

    /**
     * 初始化标签选择的位置
     */
    private void initItem() {
        itemContentFiltering = -1;
        itemComparisonOperators = -1;
        itemValueFiltering = -1;
    }

    /**
     * 标签选中监听
     */
    private OnTagSelectListener onTagSelectListener = new OnTagSelectListener() {
        @Override
        public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
            switch (parent.getId()) {
                case R.id.flt_content_filtering:
                    comparisonOperatorsAdapter.notifyDataSetChanged();
                    if (selectedList != null && selectedList.size() != 0) {
                        itemContentFiltering = selectedList.get(0);
                        etFilterValue.removeTextChangedListener(textWatcherConsumption);
                        etFilterValue.removeTextChangedListener(textWatcherPressure);
                        etFilterValue.removeTextChangedListener(textWatcherFlowRate);
                        switch (itemContentFiltering) {
                            case 0:
                                etFilterValue.setVisibility(View.VISIBLE);
                                etFilterValue.setHint(R.string.EnterTheMeterSNOrAPartOfIt);
                                etFilterValue.setText("");
                                etFilterValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                                etFilterValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                                fltValueFiltering.setVisibility(View.GONE);
                                fltComparisonOperators.getChildAt(0).setVisibility(View.GONE);
                                fltComparisonOperators.getChildAt(2).setVisibility(View.GONE);
                                break;
                            case 1:
                                etFilterValue.setVisibility(View.VISIBLE);
                                etFilterValue.setHint(R.string.EnterAUsernameOrAPartOfIt);
                                etFilterValue.setText("");
                                etFilterValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                                etFilterValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                                fltValueFiltering.setVisibility(View.GONE);
                                fltComparisonOperators.getChildAt(0).setVisibility(View.GONE);
                                fltComparisonOperators.getChildAt(2).setVisibility(View.GONE);
                                break;
                            case 2:
                                etFilterValue.setVisibility(View.VISIBLE);
                                etFilterValue.setHint(R.string.EnterTotalFlowValue);
                                etFilterValue.setText("");
                                etFilterValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
                                etFilterValue.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
                                etFilterValue.addTextChangedListener(textWatcherConsumption);
                                fltValueFiltering.setVisibility(View.GONE);
                                fltComparisonOperators.getChildAt(3).setVisibility(View.GONE);
                                break;
                            case 3:
                                etFilterValue.setVisibility(View.VISIBLE);
                                etFilterValue.setHint(R.string.EnterPressureValue);
                                etFilterValue.setText("");
                                etFilterValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                                etFilterValue.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
                                etFilterValue.addTextChangedListener(textWatcherPressure);
                                fltValueFiltering.setVisibility(View.GONE);
                                fltComparisonOperators.getChildAt(3).setVisibility(View.GONE);
                                break;
                            case 4:
                                etFilterValue.setVisibility(View.VISIBLE);
                                etFilterValue.setHint(R.string.EnterFlowRateValue);
                                etFilterValue.setText("");
                                etFilterValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                                etFilterValue.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
                                etFilterValue.addTextChangedListener(textWatcherFlowRate);
                                fltValueFiltering.setVisibility(View.GONE);
                                fltComparisonOperators.getChildAt(3).setVisibility(View.GONE);
                                break;
                            case 5:
                                valueFilteringAdapter.clearAndAddAll(spinnerMeterStatus);
                                etFilterValue.setVisibility(View.GONE);
                                fltValueFiltering.setVisibility(View.VISIBLE);
                                fltComparisonOperators.setSelectedOption(1);
                                itemComparisonOperators = 1;
                                fltComparisonOperators.getChildAt(0).setVisibility(View.GONE);
                                fltComparisonOperators.getChildAt(2).setVisibility(View.GONE);
                                fltComparisonOperators.getChildAt(3).setVisibility(View.GONE);
                                break;
                            case 6:
                                valueFilteringAdapter.clearAndAddAll(spinnerValveStatus);
                                etFilterValue.setVisibility(View.GONE);
                                fltValueFiltering.setVisibility(View.VISIBLE);
                                fltComparisonOperators.setSelectedOption(1);
                                itemComparisonOperators = 1;
                                fltComparisonOperators.getChildAt(0).setVisibility(View.GONE);
                                fltComparisonOperators.getChildAt(2).setVisibility(View.GONE);
                                fltComparisonOperators.getChildAt(3).setVisibility(View.GONE);
                                break;
                            default:
                                break;
                        }
                    } else {
                        itemContentFiltering = -1;
                    }
                    break;
                case R.id.flt_comparison_operators:
                    if (selectedList != null && selectedList.size() != 0) {
                        itemComparisonOperators = selectedList.get(0);
                    } else {
                        itemComparisonOperators = -1;
                    }
                    break;
                case R.id.flt_value_filtering:
                    if (selectedList != null && selectedList.size() != 0) {
                        itemValueFiltering = selectedList.get(0);
                    } else {
                        itemValueFiltering = -1;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 压力输入监听
     */
    private TextWatcher textWatcherPressure = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int maxPressure = 20;
            if (s.toString().contains(Constants.POINT)) {
                //如果有小数点
                //限制小数位数不大于2个
                if (s.length() - 1 - s.toString().indexOf(Constants.POINT) > maxDecimalDigits) {
                    s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                    etFilterValue.setText(s);
                    //设置光标在末尾
                    etFilterValue.setSelection(s.length());
                    showToast("最多可以输入两位小数");
                }
                if (s.toString().indexOf(Constants.POINT) > 0 && Double.valueOf(s.toString().substring(0, s.toString().indexOf(Constants.POINT))) >= maxPressure) {
                    s = s.toString().subSequence(0, s.length() - 1);
                    etFilterValue.setText(s);
                    //设置光标在末尾
                    etFilterValue.setSelection(s.length());
                    showToast("压力不能大于" + maxPressure + "bar");
                }
            } else {
                //如果没有小数点
                if (!TextUtils.isEmpty(s) && Double.valueOf(s.toString()) > maxPressure) {
                    s = s.toString().subSequence(0, s.length() - 1);
                    etFilterValue.setText(s);
                    //设置光标在末尾
                    etFilterValue.setSelection(s.length());
                    showToast("压力不能大于" + maxPressure + "bar");
                }
            }
            //如果直接输入小数点，前面自动补0
            if (s.toString().trim().equals(Constants.POINT)) {
                s = "0" + s;
                etFilterValue.setText(s);
                etFilterValue.setSelection(2);
            }
            //除了小数开头不能为0，而且开头不允许连续出现0
            if (s.toString().startsWith(Constants.ZERO_STRING) && s.toString().trim().length() > 1) {
                if (!s.toString().substring(1, 2).equals(Constants.POINT)) {
                    etFilterValue.setText(s.subSequence(0, 1));
                    etFilterValue.setSelection(1);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 累计流量输入监听
     */
    private TextWatcher textWatcherConsumption = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int maxConsumption = 99999999;
            if (s.toString().contains(Constants.POINT)) {
                //如果有小数点
                //限制小数位数不大于2个
                if (s.length() - 1 - s.toString().indexOf(Constants.POINT) > maxDecimalDigits) {
                    s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                    etFilterValue.setText(s);
                    //设置光标在末尾
                    etFilterValue.setSelection(s.length());
                    showToast("最多可以输入两位小数");
                }
                if (s.toString().indexOf(Constants.POINT) > 0 && Double.valueOf(s.toString().substring(0, s.toString().indexOf(Constants.POINT))) >= maxConsumption) {
                    s = s.toString().subSequence(0, s.length() - 1);
                    etFilterValue.setText(s);
                    //设置光标在末尾
                    etFilterValue.setSelection(s.length());
                    showToast("累计流量不能大于" + maxConsumption + "m³");
                }
            } else {
                //如果没有小数点
                if (!TextUtils.isEmpty(s) && Double.valueOf(s.toString()) > maxConsumption) {
                    s = s.toString().subSequence(0, s.length() - 1);
                    etFilterValue.setText(s);
                    //设置光标在末尾
                    etFilterValue.setSelection(s.length());
                    showToast("累计流量不能大于" + maxConsumption + "m³");
                }
            }
            //如果直接输入小数点，前面自动补0
            if (s.toString().trim().equals(Constants.POINT)) {
                s = "0" + s;
                etFilterValue.setText(s);
                etFilterValue.setSelection(2);
            }
            //除了小数开头不能为0，而且开头不允许连续出现0
            if (s.toString().startsWith(Constants.ZERO_STRING) && s.toString().trim().length() > 1) {
                if (!s.toString().substring(1, 2).equals(Constants.POINT)) {
                    etFilterValue.setText(s.subSequence(0, 1));
                    etFilterValue.setSelection(1);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 瞬时流速输入监听
     */
    private TextWatcher textWatcherFlowRate = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int maxFlowRate = 100000;
            if (s.toString().contains(Constants.POINT)) {
                //如果有小数点
                //限制小数位数不大于2个
                if (s.length() - 1 - s.toString().indexOf(Constants.POINT) > maxDecimalDigits) {
                    s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                    etFilterValue.setText(s);
                    //设置光标在末尾
                    etFilterValue.setSelection(s.length());
                    showToast("最多可以输入两位小数");
                }
                if (s.toString().indexOf(Constants.POINT) > 0 && Double.valueOf(s.toString().substring(0, s.toString().indexOf(Constants.POINT))) >= maxFlowRate) {
                    s = s.toString().subSequence(0, s.length() - 1);
                    etFilterValue.setText(s);
                    //设置光标在末尾
                    etFilterValue.setSelection(s.length());
                    showToast("瞬时流速不能大于" + maxFlowRate + "m³/h");
                }
            } else {
                //如果没有小数点
                if (!TextUtils.isEmpty(s) && Double.valueOf(s.toString()) > maxFlowRate) {
                    s = s.toString().subSequence(0, s.length() - 1);
                    etFilterValue.setText(s);
                    //设置光标在末尾
                    etFilterValue.setSelection(s.length());
                    showToast("瞬时流速不能大于100000m³/h");
                }
            }
            //如果直接输入小数点，前面自动补0
            if (s.toString().trim().equals(Constants.POINT)) {
                s = "0" + s;
                etFilterValue.setText(s);
                etFilterValue.setSelection(2);
            }
            //除了小数开头不能为0，而且开头不允许连续出现0
            if (s.toString().startsWith(Constants.ZERO_STRING) && s.toString().trim().length() > 1) {
                if (!s.toString().substring(1, 2).equals(Constants.POINT)) {
                    etFilterValue.setText(s.subSequence(0, 1));
                    etFilterValue.setSelection(1);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 查询水表的方法
     */
    public void searchWaterMeter(int searchMode) {
        String timeRange = etTimeRange.getText().toString().trim();
        if (TextUtils.isEmpty(timeRange)) {
            timeRange = (String) SharedPreferencesUtils.getInstance().getData("timeRange", SmartWaterSupply.DATA_SEARCH_TIME_RANGE);
        }
        switch (searchMode) {
            case SEARCH_MODE_NORMAL:
                //普通查询要比较页码输入框的值
                String pageNumber = etCurrentPage.getText().toString().trim();
                if (TextUtils.isEmpty(pageNumber)) {
                    currentPage = 1;
                } else if (Integer.valueOf(pageNumber) > totalPage) {
                    //查询页码大于总页码
                    if (totalPage == 0) {
                        //如果没有查询过或者查询到的是0条数据，则总页码为0，需要将查询页码设为1
                        currentPage = 1;
                    } else {
                        //如果总页码不为0，需要将查询页码设为总页码的值
                        currentPage = totalPage;
                    }
                } else {
                    currentPage = Integer.valueOf(pageNumber);
                }
                break;
            case SEARCH_MODE_CHANGE_SEGMENT:
                //切换“已抄到”和“未抄到”按钮查询时将页码设为1
                currentPage = 1;
                break;
            case SEARCH_MODE_LAST:
            case SEARCH_MODE_NEXT:
                //上下翻页查询时忽略页码输入框的值

                break;
            default:
                break;
        }
        searchMeterLastInformation(fieldName, fieldValue, timeRange, String.valueOf(currentPage - 1));
    }

    /**
     * 查询表提交信息的最后一条记录
     *
     * @param fieldName  层级名称
     * @param fieldValue 层级ID
     */
    private void searchMeterLastInformation(String fieldName, String fieldValue, String timeRange, String pageNumber) {

        String meterSizeMin = (String) SharedPreferencesUtils.getInstance().getData("MeterSize_Min", Constants.EMPTY);
        String meterSizeMax = (String) SharedPreferencesUtils.getInstance().getData("MeterSize_Max", Constants.EMPTY);
        if (Constants.EMPTY.equals(meterSizeMin) || Constants.EMPTY.equals(meterSizeMax)) {
            meterSizeMin = (String) SharedPreferencesUtils.getInstance().getData("MeterSize_Min_Default", SmartWaterSupply.DATA_SEARCH_MIN_SIZE);
            meterSizeMax = (String) SharedPreferencesUtils.getInstance().getData("MeterSize_Max_Default", SmartWaterSupply.DATA_SEARCH_MAX_SIZE);
        }
        Map<String, String> params = new HashMap<>(8);
        params.put("fieldName", fieldName);
        params.put("fieldValue", fieldValue);
        params.put("meterSizeMin", meterSizeMin);
        params.put("meterSizeMax", meterSizeMax);
        params.put("timeRange", timeRange);
        params.put("rows", String.valueOf(pageSize));
        params.put("page", pageNumber);
        params.put("param", JSON.toJSONString(filterConditionList));
        Observable<HeatMeterLastDataResult> waterMeterLastCommitInformationObservable;
        if (scvSearchMode.getSelectedIndex() == 0) {
            //查询已抄到的水表
            waterMeterLastCommitInformationObservable = NetClient.getInstances(NetClient.getBaseUrl(serverHost, httpPort, serviceName)).getNjMeterApi().searchHeatMeterLastReportYiChao(params);
        } else {
            //查询未抄到的水表
            waterMeterLastCommitInformationObservable = NetClient.getInstances(NetClient.getBaseUrl(serverHost, httpPort, serviceName)).getNjMeterApi().searchHeatMeterLastReportWeiChao(params);
        }
        waterMeterLastCommitInformationObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getNetworkSubscriber());
    }

    /**
     * 已抄到和未抄到的请求共用的NetworkSubscriber
     */
    private NetworkSubscriber<HeatMeterLastDataResult> getNetworkSubscriber() {
        return new NetworkSubscriber<HeatMeterLastDataResult>(mContext, getClass().getSimpleName()) {

            @Override
            public void onStart() {
                super.onStart();
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast("当前网络不可用，请检查网络");
                    if (!isUnsubscribed()) {
                        unsubscribe();
                    }
                } else {
                    showLoadingDialog(mContext, "查询中", true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast(responseThrowable.message);
            }

            @Override
            public void onNext(HeatMeterLastDataResult heatMeterLastDataResult) {
                cancelDialog();
                if (heatMeterLastDataResult == null) {
                    showToast("请求失败，返回值异常");
                } else {
                    if (heatMeterLastDataResult.getResult().equals(Constants.SUCCESS)) {
                        int totalCount = heatMeterLastDataResult.getCount();
                        if (totalCount % pageSize == 0) {
                            totalPage = totalCount / pageSize;
                        } else {
                            totalPage = totalCount / pageSize + 1;
                        }
                        tvMountPage.setText(String.format(getString(R.string.total_page), totalCount, totalPage));
                        etCurrentPage.setText(String.valueOf(currentPage));
                        if (totalCount == 0) {
                            //如果列表长度为0弹出提示
                            showToast("没有符合要求的表信息");
                        } else {
                            //否则显示列表内容
                            heatMeterLastDataList = heatMeterLastDataResult.getData();
                            waterMeterLastReportAdapter = new ValveLastReportAdapter(MainActivity.this, heatMeterLastDataList, pageSize, currentPage - 1);
                            lvHeatMeterLast.setAdapter(waterMeterLastReportAdapter);
                            changeListVisibility(true, false);
                        }
                    } else if (heatMeterLastDataResult.getResult().equals(Constants.FAIL)) {
                        showToast("查询失败");
                    }
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_COMPANY:
                if (RESULT_OK == resultCode) {
                    isAdminAccount = false;
                    valveAccountCorrect = true;
                    fieldName = (String) SharedPreferencesUtils.getInstance().getData(getString(R.string.fieldName), "");
                    fieldValue = String.valueOf((int) SharedPreferencesUtils.getInstance().getData(getString(R.string.fieldValue), -1));
                    searchWaterMeter(SEARCH_MODE_NORMAL);
                }
                break;
            case REQUEST_CODE_NOTIFICATION_SETTINGS:
                if (NotificationsUtils.isNotificationEnabled(mContext)) {
                    //通知权限已打开
                    showToast("已开启了通知权限");
                } else {
                    //通知权限没有打开
                    showToast("未开启通知权限，部分功能受限");
                }
                break;
            case Constants.ACTIVITY_REQUEST_CODE_100:
                if (resultCode == Constants.ACTIVITY_RESULT_CODE_100) {
                    String hierarchy = data.getStringExtra("hierarchy");
                    //如果选择的层级和当前层级不一样，则修改显示的层级、清空原查询列表并重新请求数据
                    if (!tvCurrentHierarchy.getText().toString().equals(hierarchy)) {
                        tvCurrentHierarchy.setText(data.getStringExtra("hierarchy"));
                        fieldValue = String.valueOf(data.getIntExtra("id", 0));
                        fieldName = data.getStringExtra("fieldName");
                        etCurrentPage.setText("1");
                        searchWaterMeter(SEARCH_MODE_NORMAL);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初始化默认查询配置
     */
    private void initDefaultData() {
        //每页显示条数
        pageSize = Integer.valueOf(((String) SharedPreferencesUtils.getInstance().getData("Rows_Per_Page", SmartWaterSupply.DATA_SEARCH_ROWS_PER_PAGE)));
        //查询模式
        boolean searchTypeHasRead = (boolean) SharedPreferencesUtils.getInstance().getData("Default_Search_Type_Has_Read", SmartWaterSupply.DATA_SEARCH_HAS_READ);
        if (searchTypeHasRead) {
            scvSearchMode.setSelectedIndex(0);
        } else {
            scvSearchMode.setSelectedIndex(1);
        }
        String minSize = (String) SharedPreferencesUtils.getInstance().getData("MeterSize_Min_Default", SmartWaterSupply.DATA_SEARCH_MIN_SIZE);
        String maxSize = (String) SharedPreferencesUtils.getInstance().getData("MeterSize_Max_Default", SmartWaterSupply.DATA_SEARCH_MAX_SIZE);
        tvSizeRange.setText(String.format(getString(R.string.range_meterSize_DN), minSize, maxSize));
        etTimeRange.setText((String) SharedPreferencesUtils.getInstance().getData("Search_Time_Range", SmartWaterSupply.DATA_SEARCH_TIME_RANGE));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtils.getInstance().clearData("MeterSize_Min");
        SharedPreferencesUtils.getInstance().clearData("MeterSize_Max");
        if (loginReceiver != null) {
            try {
                unregisterReceiver(loginReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
