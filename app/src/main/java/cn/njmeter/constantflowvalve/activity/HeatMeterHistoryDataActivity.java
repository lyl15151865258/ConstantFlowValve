package cn.njmeter.constantflowvalve.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.njmeter.constantflowvalve.ConstantFlowValveApplication;
import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.constant.Constants;
import cn.njmeter.constantflowvalve.constant.SmartWaterSupply;
import cn.njmeter.constantflowvalve.adapter.ValveHistoryDataCardAdapter;
import cn.njmeter.constantflowvalve.adapter.ValveHistoryDataListAdapter;
import cn.njmeter.constantflowvalve.bean.ValveCommitInformation;
import cn.njmeter.constantflowvalve.network.ExceptionHandle;
import cn.njmeter.constantflowvalve.network.NetClient;
import cn.njmeter.constantflowvalve.network.NetworkSubscriber;
import cn.njmeter.constantflowvalve.utils.ActivityController;
import cn.njmeter.constantflowvalve.utils.NetworkUtil;
import cn.njmeter.constantflowvalve.utils.SharedPreferencesUtils;
import cn.njmeter.constantflowvalve.utils.TimeUtils;
import cn.njmeter.constantflowvalve.utils.ViewUtils;
import cn.njmeter.constantflowvalve.widget.MyToolbar;
import cn.njmeter.constantflowvalve.widget.RecyclerViewDivider;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 智慧供热平台的查询历史数据页面
 * Created at 2018/5/23 0023 22:43
 *
 * @author LiYuliang
 * @version 1.0
 */

public class HeatMeterHistoryDataActivity extends BaseActivity {

    private Context mContext;
    private TextView tvDate, tvBeginTime, tvEndTime;
    private RecyclerView recyclerViewHistoryInformation;
    private ValveHistoryDataListAdapter valveHistoryDataListAdapter;
    private ValveHistoryDataCardAdapter valveHistoryDataCardAdapter;
    private LinearLayout llSingleDate, llDateInterval, llListTitle;
    private List<ValveCommitInformation.Data> dataList;
    private String serverHost, httpPort, serviceName, fieldName, fieldValue;
    private RecyclerViewDivider listDivider, cardDivider;
    private View viewSpace;
    private String meterId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_data);
        mContext = this;
        serverHost = ConstantFlowValveApplication.getInstance().getAccount().getServer_Host_XHS();
        httpPort = ConstantFlowValveApplication.getInstance().getAccount().getHttp_Port_XHS();
        serviceName = ConstantFlowValveApplication.getInstance().getAccount().getService_Name_XHS();
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, "历史数据", R.drawable.back_white, onClickListener);
        dataList = new ArrayList<>();

        recyclerViewHistoryInformation = findViewById(R.id.recyclerView_historyInformation);
        //垂直线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewHistoryInformation.setLayoutManager(linearLayoutManager);
        valveHistoryDataListAdapter = new ValveHistoryDataListAdapter(this, dataList);
        valveHistoryDataCardAdapter = new ValveHistoryDataCardAdapter(this, dataList);

        listDivider = new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, 1, ContextCompat.getColor(mContext, R.color.gray_slight));
        cardDivider = new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, 10, ContextCompat.getColor(mContext, R.color.white));

        llSingleDate = findViewById(R.id.ll_singleDate);
        llDateInterval = findViewById(R.id.ll_date_interval);
        llListTitle = findViewById(R.id.ll_listTitle);

        viewSpace = findViewById(R.id.view_space);

        fieldName = (String) SharedPreferencesUtils.getInstance().getData(getString(R.string.fieldName), "");
        fieldValue = String.valueOf((int) SharedPreferencesUtils.getInstance().getData(getString(R.string.fieldValue), -1));

        findViewById(R.id.btn_search_meter).setOnClickListener(onClickListener);
        findViewById(R.id.iv_lastDay).setOnClickListener(onClickListener);
        findViewById(R.id.iv_nextDay).setOnClickListener(onClickListener);
        tvDate = findViewById(R.id.tv_date);
        tvBeginTime = findViewById(R.id.tv_beginTime);
        tvEndTime = findViewById(R.id.tv_endTime);
        tvDate.setOnClickListener(onClickListener);
        tvDate.addTextChangedListener(textWatcher);
        tvBeginTime.setOnClickListener(onClickListener);
        tvBeginTime.addTextChangedListener(textWatcher);
        tvEndTime.setOnClickListener(onClickListener);
        tvEndTime.addTextChangedListener(textWatcher);
        tvDate.setText(TimeUtils.getCurrentDate());
        tvBeginTime.setText(TimeUtils.getCurrentDate());
        tvEndTime.setText(TimeUtils.getCurrentDate());

        initDefaultData();

        String meterId = getIntent().getStringExtra("meterId");
        if (meterId != null && Constants.METER_ID_LENGTH == meterId.length()) {
            this.meterId = meterId;
            refreshData();
        } else {
            showToast("请返回选择水表");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDefaultData();
    }

    private void initDefaultData() {
        if ((boolean) SharedPreferencesUtils.getInstance().getData("Show_Single_Date", true)) {
            llSingleDate.setVisibility(View.VISIBLE);
            llDateInterval.setVisibility(View.GONE);
        } else {
            llSingleDate.setVisibility(View.GONE);
            llDateInterval.setVisibility(View.VISIBLE);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            refreshData();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(this);
                break;
            case R.id.tv_date:
            case R.id.tv_beginTime:
            case R.id.tv_endTime:
                TimeUtils.selectData(mContext, (TextView) v);
                break;
            case R.id.btn_search_meter:
                refreshData();
                break;
            case R.id.iv_lastDay:
                ViewUtils.changeDate(mContext, tvDate, 3, -1);
                break;
            case R.id.iv_nextDay:
                ViewUtils.changeDate(mContext, tvDate, 3, 1);
                break;
            default:
                break;
        }
    };

    /**
     * 当表号、图表标题、日期发生改变或者点击刷新按钮时执行的方法
     */
    private void refreshData() {
        //清空列表视图
        if (dataList != null) {
            dataList.clear();
            showListOrCard();
        }
        if (meterId.length() == Constants.METER_ID_LENGTH) {
            searchMeterAllInformation(meterId);
        }
    }

    /**
     * 查询一只表指定时间的所有提交记录
     *
     * @param meterId 水表编号
     */
    private void searchMeterAllInformation(final String meterId) {
        String beginTime, endTime;
        if ((boolean) SharedPreferencesUtils.getInstance().getData("Show_Single_Date", true)) {
            beginTime = tvDate.getText().toString();
            endTime = tvDate.getText().toString();
        } else {
            beginTime = tvBeginTime.getText().toString();
            endTime = tvEndTime.getText().toString();
        }
        HashMap<String, String> params = new HashMap<>(5);
        params.put("fieldName", fieldName);
        params.put("fieldValue", fieldValue);
        params.put("meterId", meterId);
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        Observable<ValveCommitInformation> waterMeterCommitInformationObservable = NetClient.getInstances(NetClient.getBaseUrl(serverHost, httpPort, serviceName)).getNjMeterApi().searchMeterCommitDataInformation(params);
        waterMeterCommitInformationObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<ValveCommitInformation>(mContext, getClass().getSimpleName()) {

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
            public void onNext(ValveCommitInformation valveCommitInformation) {
                cancelDialog();
                if (valveCommitInformation == null) {
                    showToast("查询失败，返回值异常");
                } else {
                    if (Constants.SUCCESS.equals(valveCommitInformation.getResult())) {
                        //对象中拿到集合
                        dataList.clear();
                        dataList.addAll(valveCommitInformation.getData());
                        if (dataList != null && dataList.size() == 0) {
                            showToast("暂无符合要求的数据");
                        }
                        showListOrCard();
                    } else {
                        showToast("查询失败");
                    }
                }
            }
        });
    }

    private void showListOrCard() {
        recyclerViewHistoryInformation.removeItemDecoration(listDivider);
        recyclerViewHistoryInformation.removeItemDecoration(cardDivider);
        if ((boolean) SharedPreferencesUtils.getInstance().getData("Show_Data_List", SmartWaterSupply.SHOW_DATA_LIST)) {
            recyclerViewHistoryInformation.addItemDecoration(listDivider);
            recyclerViewHistoryInformation.setAdapter(valveHistoryDataListAdapter);
            valveHistoryDataListAdapter.notifyDataSetChanged();
            llListTitle.setVisibility(View.VISIBLE);
            viewSpace.setVisibility(View.GONE);
        } else {
            recyclerViewHistoryInformation.addItemDecoration(cardDivider);
            recyclerViewHistoryInformation.setAdapter(valveHistoryDataCardAdapter);
            valveHistoryDataCardAdapter.notifyDataSetChanged();
            llListTitle.setVisibility(View.GONE);
            viewSpace.setVisibility(View.VISIBLE);
        }
    }
}
