package cn.njmeter.constantflowvalve.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import cn.njmeter.constantflowvalve.ConstantFlowValveApplication;
import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.adapter.TagAdapter;
import cn.njmeter.constantflowvalve.adapter.WaterCompanyAdapter;
import cn.njmeter.constantflowvalve.bean.ValveLoginResult;
import cn.njmeter.constantflowvalve.constant.Constants;
import cn.njmeter.constantflowvalve.sqlite.DbHelper;
import cn.njmeter.constantflowvalve.sqlite.table.Table;
import cn.njmeter.constantflowvalve.utils.ActivityController;
import cn.njmeter.constantflowvalve.utils.LogUtils;
import cn.njmeter.constantflowvalve.utils.SharedPreferencesUtils;
import cn.njmeter.constantflowvalve.utils.StatusBarUtil;
import cn.njmeter.constantflowvalve.utils.ViewUtils;
import cn.njmeter.constantflowvalve.widget.MyToolbar;
import cn.njmeter.constantflowvalve.widget.CommonWarningDialog;
import cn.njmeter.constantflowvalve.widget.flowtaglayout.FlowTagLayout;
import cn.njmeter.constantflowvalve.widget.flowtaglayout.OnTagClickListener;
import cn.njmeter.constantflowvalve.widget.flowtaglayout.OnTagLongClickListener;

/**
 * 选择供热单位
 * Created by LiYuliang on 2017/3/10 0010.
 *
 * @author LiYuliang
 * @version 2017/10/26
 */

public class ChooseCompanyActivity extends BaseActivity {

    private List<ValveLoginResult.Data> dataList, platformsNameList;
    private List<String> historyList = new ArrayList<>();
    private EditText etSearch;
    private WaterCompanyAdapter waterCompanyAdapter;
    private DbHelper mDbHelper;
    private FlowTagLayout ftlHistory;
    private TagAdapter<String> historyAdapter;
    private Context context;
    private String httpPort;
    private String tableName = Table.HeatCompanySearchTable.TABLE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_company);
        context = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, "选择供热单位", R.drawable.back_white, onClickListener);
        httpPort = ConstantFlowValveApplication.getInstance().getAccount().getHttp_Port_CS();
        JsonObject jsonObject = new JsonParser().parse(getIntent().getStringExtra(getString(R.string.waterCompanyName))).getAsJsonObject();
        //Gson直接解析成对象
        ValveLoginResult valveLoginResult = new Gson().fromJson(jsonObject, ValveLoginResult.class);
        //对象中拿到集合
        dataList = valveLoginResult.getData();
        platformsNameList = new ArrayList<>();
        platformsNameList.addAll(dataList);
        historyAdapter = new TagAdapter<>(this);
        findViewById(R.id.iv_deleteSearch).setOnClickListener(onClickListener);
        findViewById(R.id.iv_deleteHistory).setOnClickListener(onClickListener);
        ftlHistory = findViewById(R.id.ftl_history);
        ftlHistory.setOnTagClickListener(onTagClickListener);
        ftlHistory.setOnTagLongClickListener(onTagLongClickListener);
        ListView listViewPlatform = findViewById(R.id.listView_platform);
        waterCompanyAdapter = new WaterCompanyAdapter(this, platformsNameList);
        listViewPlatform.setAdapter(waterCompanyAdapter);
        listViewPlatform.setOnItemClickListener(onItemClickListener);
        etSearch = findViewById(R.id.et_search);
        etSearch.addTextChangedListener(textWatcher);
        mDbHelper = ConstantFlowValveApplication.getInstance().getmDbHelper();
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorOrangePrimary);
        StatusBarUtil.setColor(this, mColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryData();
    }

    private AdapterView.OnItemClickListener onItemClickListener = (parent, view, position, id) -> {
        switch (parent.getId()) {
            case R.id.listView_platform:
                ValveLoginResult.Data data = (ValveLoginResult.Data) parent.getItemAtPosition(position);
                //判断普通管理员的最高权限
                int supplierId = data.getSupplierId();
                int exchangStationId = data.getExchangStationId();
                int villageId = data.getVillageId();
                int buildingId = data.getBuildingId();
                int entranceId = data.getEntranceId();
                String companyName = data.getSupplier();
                String fieldName = "";
                int fieldValue = -1;
                if (entranceId != -1) {
                    fieldName = "entranceId";
                    fieldValue = entranceId;
                } else if (buildingId != -1) {
                    fieldName = "buildingId";
                    fieldValue = buildingId;
                } else if (villageId != -1) {
                    fieldName = "villageId";
                    fieldValue = villageId;
                } else if (exchangStationId != -1) {
                    fieldName = "exchangStationId";
                    fieldValue = exchangStationId;
                } else if (supplierId != -1) {
                    fieldName = "supplierId";
                    fieldValue = supplierId;
                }
                SharedPreferencesUtils.getInstance().saveData(getString(R.string.waterCompanyName), companyName);
                SharedPreferencesUtils.getInstance().saveData(getString(R.string.fieldName), fieldName);
                SharedPreferencesUtils.getInstance().saveData(getString(R.string.fieldValue), fieldValue);
                SharedPreferencesUtils.getInstance().saveData(getString(R.string.supplierId), supplierId);
                SharedPreferencesUtils.getInstance().saveData(getString(R.string.exchangStationId), exchangStationId);
                SharedPreferencesUtils.getInstance().saveData(getString(R.string.villageId), villageId);
                SharedPreferencesUtils.getInstance().saveData(getString(R.string.buildingId), buildingId);
                SharedPreferencesUtils.getInstance().saveData(getString(R.string.entranceId), entranceId);
                if (!Constants.EMPTY.equals(etSearch.getText().toString()) && !hasData(etSearch.getText().toString())) {
                    insertData(etSearch.getText().toString());
                }
                setResult(RESULT_OK);
                ActivityController.finishActivity(this);
                break;
            default:
                break;
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = etSearch.getText().toString();
            platformsNameList.clear();
            for (int i = 0; i < dataList.size(); i++) {
                LogUtils.d(dataList.get(i).getSupplier());
                if (dataList.get(i).getSupplier().contains(text)) {
                    platformsNameList.add(dataList.get(i));
                }
            }
            waterCompanyAdapter.notifyDataSetChanged();
        }
    };

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(this);
                break;
            case R.id.iv_deleteSearch:
                etSearch.setText("");
                break;
            case R.id.iv_deleteHistory:
                deleteAllData();
                break;
            default:
                break;
        }
    };

    /**
     * 插入数据
     *
     * @param tempName 输入框的文字
     */
    private void insertData(String tempName) {
        ContentValues values = new ContentValues();
        values.put("name", tempName);
        values.put("httpPort", httpPort);
        mDbHelper.insert(tableName, values);
    }

    /**
     * 查询所有并显示在GridView列表上
     */
    private void queryData() {
        Cursor cursor = mDbHelper.findList(tableName, null, "httpPort = ?", new String[]{httpPort}, null, null, "id desc");
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

    /**
     * 标签点击监听
     */
    private OnTagClickListener onTagClickListener = (parent, view, position) -> {
        //获取到用户点击列表里的文字,并自动填充到搜索框内
        TextView textView = view.findViewById(R.id.tv_tag);
        etSearch.setText(textView.getText().toString());
        ViewUtils.setCharSequence(etSearch);
    };

    /**
     * 标签长按监听
     */
    private OnTagLongClickListener onTagLongClickListener = (parent, view, position) -> {
        CommonWarningDialog commonWarningDialog = new CommonWarningDialog(context, getString(R.string.warning_delete_history));
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
     * @param tempName 输入框的文字
     * @return 是否已包含
     */
    private boolean hasData(String tempName) {
        //从Record这个表里找到name=tempName的id
        Cursor cursor = mDbHelper.findList(tableName, null, "httpPort = ? and name = ?", new String[]{httpPort, tempName}, null, null, null);
        boolean hasData = cursor.moveToNext();
        cursor.close();
        return hasData;
    }

    /**
     * 清空本平台某一条搜索记录
     */
    private void deleteOneData(String name) {
        mDbHelper.delete(tableName, "name = ?", new String[]{name});
    }

    /**
     * 清空本平台下保存的搜索记录
     */
    private void deleteAllData() {
        CommonWarningDialog commonWarningDialog = new CommonWarningDialog(context, getString(R.string.warning_delete_history_all));
        commonWarningDialog.setCancelable(false);
        commonWarningDialog.setOnDialogClickListener(new CommonWarningDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                mDbHelper.delete(tableName, "httpPort = ?", new String[]{httpPort});
                queryData();
            }

            @Override
            public void onCancelClick() {
            }
        });
        commonWarningDialog.show();
    }

}
