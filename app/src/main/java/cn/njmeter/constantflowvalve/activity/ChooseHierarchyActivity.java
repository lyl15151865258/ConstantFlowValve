package cn.njmeter.constantflowvalve.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.njmeter.constantflowvalve.ConstantFlowValveApplication;
import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.adapter.HierarchyAdapter;
import cn.njmeter.constantflowvalve.adapter.TagAdapter;
import cn.njmeter.constantflowvalve.constant.Constants;
import cn.njmeter.constantflowvalve.bean.Hierarchy;
import cn.njmeter.constantflowvalve.bean.HeatCompanyHierarchy;
import cn.njmeter.constantflowvalve.network.ExceptionHandle;
import cn.njmeter.constantflowvalve.network.NetClient;
import cn.njmeter.constantflowvalve.network.NetworkSubscriber;
import cn.njmeter.constantflowvalve.sqlite.DbHelper;
import cn.njmeter.constantflowvalve.sqlite.table.Table;
import cn.njmeter.constantflowvalve.utils.ActivityController;
import cn.njmeter.constantflowvalve.utils.NetworkUtil;
import cn.njmeter.constantflowvalve.utils.SharedPreferencesUtils;
import cn.njmeter.constantflowvalve.widget.MyToolbar;
import cn.njmeter.constantflowvalve.widget.CommonWarningDialog;
import cn.njmeter.constantflowvalve.widget.flowtaglayout.FlowTagLayout;
import cn.njmeter.constantflowvalve.widget.flowtaglayout.OnTagClickListener;
import cn.njmeter.constantflowvalve.widget.flowtaglayout.OnTagLongClickListener;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 选择水司层级
 * Created by LiYuliang on 2017/8/31 0031.
 *
 * @author LiYuliang
 * @version 2017/10/25
 */

public class ChooseHierarchyActivity extends BaseActivity {

    private Context mContext;
    private List<HeatCompanyHierarchy.Data> companyHierarchyList = new ArrayList<>();
    private List<Hierarchy> hierarchyList = new ArrayList<>();
    private List<String> historyList = new ArrayList<>();
    private ListView lvHierarchy;
    private EditText etSearch;
    private HierarchyAdapter hierarchyAdapter;
    private DbHelper mDbHelper;
    private FlowTagLayout ftlHistory;
    private TagAdapter<String> historyAdapter;
    private String serverHost, httpPort, serviceName, fieldName;
    private int fieldValue, exchangStationId, villageId, buildingId, entranceId;
    private TextView tvExchangeStation, tvVillage, tvBuilding, tvEntrance;
    private Hierarchy currentHierarchy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_company_hierarchy);
        MyToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.initToolBar(this, toolbar, "选择区域层级", R.drawable.back_white, onClickListener);
        mContext = this;
        serverHost = ConstantFlowValveApplication.getInstance().getAccount().getServer_Host_XHS();
        httpPort = ConstantFlowValveApplication.getInstance().getAccount().getHttp_Port_XHS();
        serviceName = ConstantFlowValveApplication.getInstance().getAccount().getService_Name_XHS();
        fieldName = (String) SharedPreferencesUtils.getInstance().getData(getString(R.string.fieldName), "");
        fieldValue = (int) SharedPreferencesUtils.getInstance().getData(getString(R.string.fieldValue), 0);
        exchangStationId = (int) SharedPreferencesUtils.getInstance().getData(getString(R.string.exchangStationId), -1);
        villageId = (int) SharedPreferencesUtils.getInstance().getData(getString(R.string.villageId), -1);
        buildingId = (int) SharedPreferencesUtils.getInstance().getData(getString(R.string.buildingId), -1);
        entranceId = (int) SharedPreferencesUtils.getInstance().getData(getString(R.string.entranceId), -1);
        lvHierarchy = findViewById(R.id.lv_hierarchy);
        hierarchyAdapter = new HierarchyAdapter(this, hierarchyList);
        historyAdapter = new TagAdapter<>(this);
        lvHierarchy.setAdapter(hierarchyAdapter);
        lvHierarchy.setOnItemClickListener(onItemClickListener);
        searchAllHierarchy();
        ImageView ivDeleteSearch = findViewById(R.id.iv_deleteSearch);
        ImageView ivDeleteHistory = findViewById(R.id.iv_deleteHistory);
        ivDeleteSearch.setOnClickListener(onClickListener);
        ivDeleteHistory.setOnClickListener(onClickListener);
        etSearch = findViewById(R.id.et_search);
        etSearch.addTextChangedListener(textWatcher);
        ftlHistory = findViewById(R.id.ftl_history);
        ftlHistory.setOnTagClickListener(onTagClickListener);
        ftlHistory.setOnTagLongClickListener(onTagLongClickListener);
        tvExchangeStation = findViewById(R.id.tv_exchangeStation);
        tvVillage = findViewById(R.id.tv_village);
        tvBuilding = findViewById(R.id.tv_building);
        tvEntrance = findViewById(R.id.tv_entrance);
        (findViewById(R.id.btn_clear)).setOnClickListener(onClickListener);
        (findViewById(R.id.btn_determine)).setOnClickListener(onClickListener);
        tvExchangeStation.setOnClickListener(onClickListener);
        tvVillage.setOnClickListener(onClickListener);
        tvBuilding.setOnClickListener(onClickListener);
        tvEntrance.setOnClickListener(onClickListener);
        mDbHelper = ConstantFlowValveApplication.getInstance().getmDbHelper();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //查询显示历史搜索信息
        queryData();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentHierarchy = (Hierarchy) parent.getItemAtPosition(position);
            int type = currentHierarchy.getType();
            HeatCompanyHierarchy.Data companyHierarchy = (new HeatCompanyHierarchy()).new Data();
            switch (type) {
                case Hierarchy.EXCHANGE_STATION:
                    //如果点击的是分公司层级
                    for (int i = 0; i < companyHierarchyList.size(); i++) {
                        if (companyHierarchyList.get(i).getExchangStationId() == currentHierarchy.getId()) {
                            companyHierarchy = companyHierarchyList.get(i);
                            break;
                        }
                    }
                    tvExchangeStation.setText(companyHierarchy.getExchangStation());

                    showSuitedHierarchy(tvVillage);
                    break;
                case Hierarchy.VILLAGE:
                    //如果点击的是小区层级
                    for (int i = 0; i < companyHierarchyList.size(); i++) {
                        if (companyHierarchyList.get(i).getVillageId() == currentHierarchy.getId()) {
                            companyHierarchy = companyHierarchyList.get(i);
                            break;
                        }
                    }
                    tvExchangeStation.setText(companyHierarchy.getExchangStation());
                    tvVillage.setText(companyHierarchy.getVillage());

                    showSuitedHierarchy(tvBuilding);
                    break;
                case Hierarchy.BUILDING:
                    //如果点击的是楼栋层级
                    for (int i = 0; i < companyHierarchyList.size(); i++) {
                        if (companyHierarchyList.get(i).getBuildingId() == currentHierarchy.getId()) {
                            companyHierarchy = companyHierarchyList.get(i);
                            break;
                        }
                    }
                    tvExchangeStation.setText(companyHierarchy.getExchangStation());
                    tvVillage.setText(companyHierarchy.getVillage());
                    tvBuilding.setText(companyHierarchy.getBuilding());

                    showSuitedHierarchy(tvEntrance);
                    break;
                case Hierarchy.ENTRANCE:
                    //如果点击的是单元层级
                    for (int i = 0; i < companyHierarchyList.size(); i++) {
                        if (companyHierarchyList.get(i).getEntranceId() == currentHierarchy.getId()) {
                            companyHierarchy = companyHierarchyList.get(i);
                            break;
                        }
                    }
                    tvExchangeStation.setText(companyHierarchy.getExchangStation());
                    tvVillage.setText(companyHierarchy.getVillage());
                    tvBuilding.setText(companyHierarchy.getBuilding());
                    tvEntrance.setText(companyHierarchy.getEntrance());
                    break;
                default:
                    break;
            }
            //保存到搜索记录
            if (!"".equals(etSearch.getText().toString()) && !hasData(etSearch.getText().toString())) {
                insertData(etSearch.getText().toString());
            }
        }
    };

    /**
     * 查询水司所有层级
     */
    private void searchAllHierarchy() {
        Map<String, String> params = new HashMap<>(2);
        params.put("fieldName", fieldName);
        params.put("fieldValue", String.valueOf(fieldValue));
        Observable<HeatCompanyHierarchy> waterCompanyHierarchyObservable = NetClient.getInstances(NetClient.getBaseUrl(serverHost, httpPort, serviceName)).getNjMeterApi().searchAllHierarchy(params);
        waterCompanyHierarchyObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<HeatCompanyHierarchy>(mContext, getClass().getSimpleName()) {

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
            public void onNext(HeatCompanyHierarchy heatCompanyHierarchy) {
                cancelDialog();
                if (heatCompanyHierarchy == null) {
                    showToast("查询失败，返回值异常");
                } else {
                    if (Constants.SUCCESS.equals(heatCompanyHierarchy.getResult())) {
                        companyHierarchyList = heatCompanyHierarchy.getData();
                        if (companyHierarchyList.size() != 0) {
                            showTextAndList();
                        } else {
                            showToast("没有查询到层级信息");
                        }
                    } else {
                        showToast("没有查询到层级信息");
                    }
                }
            }
        });
    }

    private void showTextAndList() {
        if (entranceId != -1) {
            tvExchangeStation.setText(companyHierarchyList.get(0).getExchangStation());
            tvVillage.setText(companyHierarchyList.get(0).getVillage());
            tvBuilding.setText(companyHierarchyList.get(0).getBuilding());
            tvEntrance.setText(companyHierarchyList.get(0).getEntrance());
        } else if (buildingId != -1) {
            tvExchangeStation.setText(companyHierarchyList.get(0).getExchangStation());
            tvVillage.setText(companyHierarchyList.get(0).getVillage());
            tvBuilding.setText(companyHierarchyList.get(0).getBuilding());
            showSuitedHierarchy(tvEntrance);
        } else if (villageId != -1) {
            tvExchangeStation.setText(companyHierarchyList.get(0).getExchangStation());
            tvVillage.setText(companyHierarchyList.get(0).getVillage());
            showSuitedHierarchy(tvBuilding);
        } else if (exchangStationId != -1) {
            tvExchangeStation.setText(companyHierarchyList.get(0).getExchangStation());
            showSuitedHierarchy(tvVillage);
        } else {
            showSuitedHierarchy(tvExchangeStation);
        }
    }

    /**
     * 插入数据
     *
     * @param tempName 需要插入的数据
     */
    private void insertData(String tempName) {
        ContentValues values = new ContentValues();
        values.put("name", tempName);
        values.put("fieldName", fieldName);
        values.put("fieldValue", String.valueOf(fieldValue));
        mDbHelper.insert(Table.HierarchySearchTable.TABLE_NAME, values);
        queryData();
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s.toString())) {
                clearHierarchy();
            } else {
                showHierarchyListView();
            }
        }
    };

    /**
     * 展示层级结果
     */
    private void showHierarchyListView() {
        String text = etSearch.getText().toString();
        hierarchyList.clear();
        for (int i = 0; i < companyHierarchyList.size(); i++) {
            if (exchangStationId == -1 && companyHierarchyList.get(i).getExchangStation().contains(text)) {
                //分公司层级包含该字段
                Hierarchy hierarchy = new Hierarchy();
                hierarchy.setId(companyHierarchyList.get(i).getExchangStationId());
                hierarchy.setType(Hierarchy.EXCHANGE_STATION);
                hierarchy.setText("（分公司）" + companyHierarchyList.get(i).getExchangStation());
                if (!hierarchyList.contains(hierarchy)) {
                    hierarchyList.add(hierarchy);
                }
            }
            if (villageId == -1 && companyHierarchyList.get(i).getVillage().contains(text)) {
                //小区层级包含该字段
                Hierarchy hierarchy = new Hierarchy();
                hierarchy.setId(companyHierarchyList.get(i).getVillageId());
                hierarchy.setType(Hierarchy.VILLAGE);
                hierarchy.setText("（小区）" + companyHierarchyList.get(i).getVillage());
                if (!hierarchyList.contains(hierarchy)) {
                    hierarchyList.add(hierarchy);
                }
            }
            if (buildingId == -1 && companyHierarchyList.get(i).getBuilding().contains(text)) {
                //楼栋层级包含该字段
                Hierarchy hierarchy = new Hierarchy();
                hierarchy.setId(companyHierarchyList.get(i).getBuildingId());
                hierarchy.setType(Hierarchy.BUILDING);
                hierarchy.setText("（楼栋）" + companyHierarchyList.get(i).getBuilding());
                if (!hierarchyList.contains(hierarchy)) {
                    hierarchyList.add(hierarchy);
                }
            }
            if (entranceId == -1 && companyHierarchyList.get(i).getEntrance().contains(text)) {
                //单元层级包含该字段
                Hierarchy hierarchy = new Hierarchy();
                hierarchy.setId(companyHierarchyList.get(i).getEntranceId());
                hierarchy.setType(Hierarchy.ENTRANCE);
                hierarchy.setText("（单元）" + companyHierarchyList.get(i).getEntrance());
                if (!hierarchyList.contains(hierarchy)) {
                    hierarchyList.add(hierarchy);
                }
            }
        }
        hierarchyAdapter = new HierarchyAdapter(ChooseHierarchyActivity.this, hierarchyList);
        lvHierarchy.removeAllViewsInLayout();
        lvHierarchy.setAdapter(hierarchyAdapter);
        lvHierarchy.deferNotifyDataSetChanged();
        if (exchangStationId != -1) {
            tvExchangeStation.setText(companyHierarchyList.get(0).getExchangStation());
        }
        if (villageId != -1) {
            tvVillage.setText(companyHierarchyList.get(0).getVillage());
        }
        if (buildingId != -1) {
            tvBuilding.setText(companyHierarchyList.get(0).getBuilding());
        }
        if (entranceId != -1) {
            tvEntrance.setText(companyHierarchyList.get(0).getEntrance());
        }
    }

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(this);
                break;
            case R.id.iv_deleteSearch:
                etSearch.setText("");
                break;
            case R.id.iv_deleteHistory:
                deleteData();
                break;
            case R.id.btn_clear:
                etSearch.setText("");
                clearHierarchy();
                break;
            case R.id.btn_determine:
                String hierarchy, name;
                int id;
                if (currentHierarchy != null) {
                    int type = currentHierarchy.getType();
                    HeatCompanyHierarchy.Data companyHierarchy = (new HeatCompanyHierarchy()).new Data();
                    id = currentHierarchy.getId();
                    switch (type) {
                        case Hierarchy.EXCHANGE_STATION:
                            //如果是分公司层级，则直接显示分公司信息
                            for (int i = 0; i < companyHierarchyList.size(); i++) {
                                if (companyHierarchyList.get(i).getExchangStationId() == currentHierarchy.getId()) {
                                    companyHierarchy = companyHierarchyList.get(i);
                                    break;
                                }
                            }
                            hierarchy = companyHierarchy.getExchangStation() + "—全部";
                            name = "exchangStationId";
                            break;
                        case Hierarchy.VILLAGE:
                            //如果是楼栋层级，则需要查询显示上一级分公司信息
                            for (int i = 0; i < companyHierarchyList.size(); i++) {
                                if (companyHierarchyList.get(i).getVillageId() == currentHierarchy.getId()) {
                                    companyHierarchy = companyHierarchyList.get(i);
                                    break;
                                }
                            }
                            hierarchy = companyHierarchy.getExchangStation() + "—" + companyHierarchy.getVillage() + "—全部";
                            name = "villageId";
                            break;
                        case Hierarchy.BUILDING:
                            //如果是楼栋层级，则需要查询显示上一级分公司信息
                            for (int i = 0; i < companyHierarchyList.size(); i++) {
                                if (companyHierarchyList.get(i).getBuildingId() == currentHierarchy.getId()) {
                                    companyHierarchy = companyHierarchyList.get(i);
                                    break;
                                }
                            }
                            hierarchy = companyHierarchy.getExchangStation() + "—" + companyHierarchy.getVillage() + "—" + companyHierarchy.getBuilding() + "—全部";
                            name = "buildingId";
                            break;
                        case Hierarchy.ENTRANCE:
                            //如果是楼栋层级，则需要查询显示上一级分公司信息
                            for (int i = 0; i < companyHierarchyList.size(); i++) {
                                if (companyHierarchyList.get(i).getEntranceId() == currentHierarchy.getId()) {
                                    companyHierarchy = companyHierarchyList.get(i);
                                    break;
                                }
                            }
                            hierarchy = companyHierarchy.getExchangStation() + "—" + companyHierarchy.getVillage() + "—" + companyHierarchy.getBuilding() + "—" + companyHierarchy.getEntrance();
                            name = "entranceId";
                            break;
                        default:
                            hierarchy = "全部";
                            name = "";
                            break;
                    }
                } else {
                    hierarchy = "全部";
                    id = fieldValue;
                    name = fieldName;
                }
                Intent intent = new Intent();
                intent.putExtra("hierarchy", hierarchy);
                intent.putExtra("fieldName", name);
                intent.putExtra("id", id);
                setResult(100, intent);
                ActivityController.finishActivity(ChooseHierarchyActivity.this);
                break;
            case R.id.tv_exchangeStation:
                if (exchangStationId == -1) {
                    showSuitedHierarchy(v);
                } else {
                    showToast("您无权修改该层级");
                }
                break;
            case R.id.tv_village:
                if (villageId == -1) {
                    showSuitedHierarchy(v);
                } else {
                    showToast("您无权修改该层级");
                }
                break;
            case R.id.tv_building:
                if (buildingId == -1) {
                    showSuitedHierarchy(v);
                } else {
                    showToast("您无权修改该层级");
                }
                break;
            case R.id.tv_entrance:
                if (entranceId == -1) {
                    showSuitedHierarchy(v);
                } else {
                    showToast("您无权修改该层级");
                }
                break;
            default:
                break;
        }
    };

    /**
     * 清除已选层级
     */
    private void clearHierarchy() {
        currentHierarchy = null;
        if (entranceId != -1) {
            tvExchangeStation.setText(companyHierarchyList.get(0).getExchangStation());
            tvVillage.setText(companyHierarchyList.get(0).getVillage());
            tvBuilding.setText(companyHierarchyList.get(0).getBuilding());
            tvEntrance.setText(companyHierarchyList.get(0).getEntrance());
        } else if (buildingId != -1) {
            tvExchangeStation.setText(companyHierarchyList.get(0).getExchangStation());
            tvVillage.setText(companyHierarchyList.get(0).getVillage());
            tvBuilding.setText(companyHierarchyList.get(0).getBuilding());
            tvEntrance.setText("");
            showSuitedHierarchy(tvEntrance);
        } else if (villageId != -1) {
            tvExchangeStation.setText(companyHierarchyList.get(0).getExchangStation());
            tvVillage.setText(companyHierarchyList.get(0).getVillage());
            tvBuilding.setText("");
            tvEntrance.setText("");
            showSuitedHierarchy(tvBuilding);
        } else if (exchangStationId != -1) {
            tvExchangeStation.setText(companyHierarchyList.get(0).getExchangStation());
            tvVillage.setText("");
            tvBuilding.setText("");
            tvEntrance.setText("");
            showSuitedHierarchy(tvVillage);
        } else {
            tvExchangeStation.setText("");
            tvVillage.setText("");
            tvBuilding.setText("");
            tvEntrance.setText("");
            showSuitedHierarchy(tvExchangeStation);
        }
    }

    /**
     * 点击层级选项显示对应的层级list
     *
     * @param view View控件
     */
    private void showSuitedHierarchy(View view) {
        hierarchyList.clear();
        switch (view.getId()) {
            case R.id.tv_exchangeStation:
                for (int i = 0; i < companyHierarchyList.size(); i++) {
                    HeatCompanyHierarchy.Data companyHierarchy = companyHierarchyList.get(i);
                    Hierarchy hierarchy = new Hierarchy();
                    hierarchy.setId(companyHierarchy.getExchangStationId());
                    hierarchy.setType(Hierarchy.EXCHANGE_STATION);
                    hierarchy.setText("（分公司）" + companyHierarchy.getExchangStation());
                    if (!hierarchyList.contains(hierarchy)) {
                        hierarchyList.add(hierarchy);
                    }
                }
                break;
            case R.id.tv_village:
                for (int i = 0; i < companyHierarchyList.size(); i++) {
                    HeatCompanyHierarchy.Data companyHierarchy = companyHierarchyList.get(i);
                    Hierarchy hierarchy = new Hierarchy();
                    hierarchy.setId(companyHierarchy.getVillageId());
                    hierarchy.setType(Hierarchy.VILLAGE);
                    hierarchy.setText("（小区）" + companyHierarchy.getVillage());
                    if (!hierarchyList.contains(hierarchy)) {
                        if (!TextUtils.isEmpty(tvExchangeStation.getText().toString())) {
                            if (companyHierarchy.getExchangStation().equals(tvExchangeStation.getText().toString())) {
                                hierarchyList.add(hierarchy);
                            }
                        } else {
                            hierarchyList.add(hierarchy);
                        }
                    }
                }
                break;
            case R.id.tv_building:
                for (int i = 0; i < companyHierarchyList.size(); i++) {
                    HeatCompanyHierarchy.Data companyHierarchy = companyHierarchyList.get(i);
                    Hierarchy hierarchy = new Hierarchy();
                    hierarchy.setId(companyHierarchy.getBuildingId());
                    hierarchy.setType(Hierarchy.BUILDING);
                    hierarchy.setText("（楼栋）" + companyHierarchy.getBuilding());
                    if (!hierarchyList.contains(hierarchy)) {
                        if (!TextUtils.isEmpty(tvVillage.getText().toString())) {
                            if (companyHierarchy.getVillage().equals(tvVillage.getText().toString())) {
                                hierarchyList.add(hierarchy);
                            }
                        } else if (!TextUtils.isEmpty(tvExchangeStation.getText().toString())) {
                            if (companyHierarchy.getExchangStation().equals(tvExchangeStation.getText().toString())) {
                                hierarchyList.add(hierarchy);
                            }
                        } else {
                            hierarchyList.add(hierarchy);
                        }
                    }
                }
                break;
            case R.id.tv_entrance:
                for (int i = 0; i < companyHierarchyList.size(); i++) {
                    HeatCompanyHierarchy.Data companyHierarchy = companyHierarchyList.get(i);
                    Hierarchy hierarchy = new Hierarchy();
                    hierarchy.setId(companyHierarchy.getEntranceId());
                    hierarchy.setType(Hierarchy.ENTRANCE);
                    hierarchy.setText("（单元）" + companyHierarchy.getEntrance());
                    if (!hierarchyList.contains(hierarchy)) {
                        if (!TextUtils.isEmpty(tvBuilding.getText().toString())) {
                            if (companyHierarchy.getBuilding().equals(tvBuilding.getText().toString())) {
                                hierarchyList.add(hierarchy);
                            }
                        } else if (!TextUtils.isEmpty(tvVillage.getText().toString())) {
                            if (companyHierarchy.getVillage().equals(tvVillage.getText().toString())) {
                                hierarchyList.add(hierarchy);
                            }
                        } else if (!TextUtils.isEmpty(tvExchangeStation.getText().toString())) {
                            if (companyHierarchy.getExchangStation().equals(tvExchangeStation.getText().toString())) {
                                hierarchyList.add(hierarchy);
                            }
                        } else {
                            hierarchyList.add(hierarchy);
                        }
                    }
                }
                break;
            default:
                break;
        }
        hierarchyAdapter = new HierarchyAdapter(ChooseHierarchyActivity.this, hierarchyList);
        lvHierarchy.removeAllViewsInLayout();
        lvHierarchy.setAdapter(hierarchyAdapter);
        lvHierarchy.deferNotifyDataSetChanged();
    }

    /**
     * 模糊查询数据 并显示在GridView列表上
     */
    private void queryData() {
        //模糊搜索
        Cursor cursor = mDbHelper.findList(Table.HierarchySearchTable.TABLE_NAME, null, "fieldName = ? and fieldValue = ?", new String[]{fieldName, String.valueOf(fieldValue)}, null, null, null);
        // 清空list
        historyList.clear();
        // 查询到的数据添加到list集合
        while (cursor.moveToNext()) {
            String history = cursor.getString(1);
            historyList.add(history);
        }
        ftlHistory.setAdapter(historyAdapter);
        historyAdapter.clearAndAddAll(historyList);
        historyAdapter.notifyDataSetChanged();
        if (historyList.size() == 0) {
            ftlHistory.setVisibility(View.GONE);
        } else {
            ftlHistory.setVisibility(View.VISIBLE);
        }
        cursor.close();
    }

    private OnTagClickListener onTagClickListener = new OnTagClickListener() {
        @Override
        public void onItemClick(FlowTagLayout parent, View view, int position) {
            //获取到用户点击列表里的文字,并自动填充到搜索框内
            TextView textView = view.findViewById(R.id.tv_tag);
            etSearch.setText(textView.getText().toString());
        }
    };

    /**
     * 标签长按监听
     */
    private OnTagLongClickListener onTagLongClickListener = (parent, view, position) -> {
        CommonWarningDialog commonWarningDialog = new CommonWarningDialog(mContext, getString(R.string.warning_delete_history));
        commonWarningDialog.setCancelable(false);
        commonWarningDialog.setOnDialogClickListener(new CommonWarningDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                deleteOneData(((TextView) view.findViewById(R.id.tv_tag)).getText().toString());
                queryData();
            }

            @Override
            public void onCancelClick() {
            }
        });
        commonWarningDialog.show();
    };

    /**
     * 检查数据库中是否已经有该条记录
     *
     * @param tempName 输入框内容
     * @return 是否包含
     */
    private boolean hasData(String tempName) {
        //从Record这个表里找到name=tempName的id
        Cursor cursor = mDbHelper.findList(Table.HierarchySearchTable.TABLE_NAME, null, "name = ? and fieldName = ? and fieldValue = ?", new String[]{tempName, fieldName, String.valueOf(fieldValue)}, null, null, null);
        boolean hasData = cursor.moveToNext();
        cursor.close();
        return hasData;
    }

    /**
     * 清空本水司下某一条搜索记录
     */
    private void deleteOneData(String name) {
        mDbHelper.delete(Table.HierarchySearchTable.TABLE_NAME, "name = ? and fieldName = ? and fieldValue = ?", new String[]{name, fieldName, String.valueOf(fieldValue)});
    }

    /**
     * 清空数据
     */
    private void deleteData() {
        CommonWarningDialog commonWarningDialog = new CommonWarningDialog(mContext, getString(R.string.warning_delete_history_all));
        commonWarningDialog.setCancelable(false);
        commonWarningDialog.setOnDialogClickListener(new CommonWarningDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                mDbHelper.delete(Table.HierarchySearchTable.TABLE_NAME, null, null);
                queryData();
            }

            @Override
            public void onCancelClick() {
            }
        });
        commonWarningDialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        queryData();
    }

}
