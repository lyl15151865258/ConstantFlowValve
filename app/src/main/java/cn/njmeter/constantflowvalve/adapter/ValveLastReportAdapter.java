package cn.njmeter.constantflowvalve.adapter;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.activity.CommunicationServiceActivity;
import cn.njmeter.constantflowvalve.activity.HeatMeterHistoryDataActivity;
import cn.njmeter.constantflowvalve.bean.HeatMeterLastData;

/**
 * 水表最后一次抄表信息扩展列表的适配器
 * Created by LiYuliang on 2017/12/28 0028.
 *
 * @author LiYuliang
 * @version 2018/01/11
 */

public class ValveLastReportAdapter extends BaseExpandableListAdapter {

    private List<HeatMeterLastData> dataList;
    private AppCompatActivity appCompatActivity;
    private int pageSize, pageNumber;

    public ValveLastReportAdapter(AppCompatActivity appCompatActivity, List<HeatMeterLastData> dataList, int pageSize, int pageNumber) {
        this.appCompatActivity = appCompatActivity;
        this.dataList = dataList;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return dataList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return dataList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dataList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentViewHolder parentViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(appCompatActivity).inflate(R.layout.item_information_water_meter_last, parent, false);
            parentViewHolder = new ParentViewHolder(convertView);
            convertView.setTag(parentViewHolder);
        } else {
            parentViewHolder = (ParentViewHolder) convertView.getTag();
        }
        HeatMeterLastData data = dataList.get(groupPosition);
        parentViewHolder.tvNumber.setText(String.valueOf(pageNumber * pageSize + groupPosition + 1));
        parentViewHolder.tvMeterId.setText(data.getMeterId());
        parentViewHolder.tvUserName.setText(data.getUserName());
        parentViewHolder.tvConsumption.setText(String.valueOf(data.getTotal()));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(appCompatActivity).inflate(R.layout.item_heat_meter_data_last, parent, false);
            childViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        HeatMeterLastData data = dataList.get(groupPosition);
        childViewHolder.tvMeterId.setText(data.getMeterId());
        childViewHolder.tvMeterSize.setText(data.getMeterSize());
        childViewHolder.tvFlowRate.setText(data.getFlowPoint() + "L/h");
        childViewHolder.tvTurns.setText(String.valueOf(data.getRotateNum()));
        childViewHolder.tvFlow.setText(String.format(appCompatActivity.getString(R.string.exampleConsumption), String.valueOf(data.getTotal())));
        childViewHolder.tvTemperature.setText(String.format(appCompatActivity.getString(R.string.example_temperature), String.valueOf(data.getT2Inp())));
        childViewHolder.tvSleepTime.setText(data.getStartDormancyTime() + "月~" + data.getEndDormancyTime() + "月");
        childViewHolder.tvUploadInterval.setText(data.getUploadInterval() + "小时");
        childViewHolder.tvValveStatus.setText(data.getValveStatus());
        childViewHolder.tvMeterStatus.setText(data.getStatus());
        childViewHolder.tvCreateTime.setText(data.getSampleTime());
        childViewHolder.tvCommitTime.setText(data.getCreateTime());
        String address = data.getVillage() + data.getBuilding() + data.getEntrance() + data.getDoorPlate();
        childViewHolder.tvAddress.setText(address);

        childViewHolder.btnHistoryData.setOnClickListener((view) -> {
            String meterId = data.getMeterId();
            Intent intent = new Intent(appCompatActivity, HeatMeterHistoryDataActivity.class);
            intent.putExtra("meterId", meterId);
            appCompatActivity.startActivity(intent);
        });

        childViewHolder.btnOperateMeter.setOnClickListener((view) -> {
            String meterId = data.getMeterId();
            String imei = data.getImei();
            Intent intent = new Intent(appCompatActivity, CommunicationServiceActivity.class);
            intent.putExtra("meterId", meterId);
            intent.putExtra("imei", imei);
            appCompatActivity.startActivity(intent);
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    private class ParentViewHolder {
        private TextView tvNumber, tvMeterId, tvUserName, tvConsumption;

        private ParentViewHolder(View view) {
            tvNumber = view.findViewById(R.id.tv_number);
            tvMeterId = view.findViewById(R.id.tv_meterId);
            tvUserName = view.findViewById(R.id.tv_userName);
            tvConsumption = view.findViewById(R.id.tv_consumption);
        }
    }

    private class ChildViewHolder {
        private TextView tvMeterId, tvMeterSize, tvFlowRate, tvTurns, tvFlow, tvTemperature, tvValveStatus,
                tvMeterStatus, tvSleepTime, tvUploadInterval, tvCreateTime, tvCommitTime, tvAddress;
        private Button btnHistoryData, btnOperateMeter;

        private ChildViewHolder(View view) {
            tvMeterId = view.findViewById(R.id.tvMeterId);
            tvMeterSize = view.findViewById(R.id.tvMeterSize);
            tvFlowRate = view.findViewById(R.id.tvFlowRate);
            tvTurns = view.findViewById(R.id.tvTurns);
            tvFlow = view.findViewById(R.id.tvFlow);
            tvTemperature = view.findViewById(R.id.tvTemperature);
            tvValveStatus = view.findViewById(R.id.tvValveStatus);
            tvMeterStatus = view.findViewById(R.id.tvMeterStatus);
            tvSleepTime = view.findViewById(R.id.tvSleepTime);
            tvUploadInterval = view.findViewById(R.id.tvUploadInterval);
            tvCreateTime = view.findViewById(R.id.tvCreateTime);
            tvCommitTime = view.findViewById(R.id.tvCommitTime);
            tvAddress = view.findViewById(R.id.tv_address);
            btnHistoryData = view.findViewById(R.id.btn_historyData);
            btnOperateMeter = view.findViewById(R.id.btn_operateMeter);
        }
    }
}