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
import cn.njmeter.constantflowvalve.bean.HeatMeterLastData;

/**
 * 水表最后一次抄表信息扩展列表的适配器
 * Created by LiYuliang on 2017/12/28 0028.
 *
 * @author LiYuliang
 * @version 2018/01/11
 */

public class HeatMeterLastReportAdapter extends BaseExpandableListAdapter {

    private List<HeatMeterLastData> dataList;
    private AppCompatActivity appCompatActivity;
    private int pageSize, pageNumber;

    public HeatMeterLastReportAdapter(AppCompatActivity appCompatActivity, List<HeatMeterLastData> dataList, int pageSize, int pageNumber) {
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
        childViewHolder.tvConsumptionPositive.setText(String.format(appCompatActivity.getString(R.string.exampleConsumption), String.valueOf(data.getTotal())));
        childViewHolder.tvConsumptionReserve.setText(String.format(appCompatActivity.getString(R.string.exampleConsumption), String.valueOf(data.getElectric())));
        childViewHolder.tvFlowRate.setText(String.format(appCompatActivity.getString(R.string.exampleFlowRate), String.valueOf(data.getFlowRate())));
        childViewHolder.tvPressure.setText(String.format(appCompatActivity.getString(R.string.examplePressureBar), String.valueOf(data.getPressure())));
        childViewHolder.tvValveStatus.setText(data.getValveStatus());
        childViewHolder.tvMeterStatus.setText(data.getStatus());
        childViewHolder.tvCreateTime.setText(data.getSampleTime());
        childViewHolder.tvCommitTime.setText(data.getCreateTime());
        String address = data.getVillage() + data.getBuilding() + data.getEntrance() + data.getDoorPlate();
        childViewHolder.tvAddress.setText(address);

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
        private TextView tvMeterId, tvMeterSize, tvConsumptionPositive, tvConsumptionReserve, tvFlowRate, tvPressure, tvValveStatus,
                tvMeterStatus, tvCreateTime, tvCommitTime, tvAddress;
        private Button btnHistoryData, btnShowWaterStatistics, btnShowDataCharts, btnOperateMeter;

        private ChildViewHolder(View view) {
            tvMeterId = view.findViewById(R.id.tv_meter_id);
            tvMeterSize = view.findViewById(R.id.tv_meterSize);
            tvConsumptionPositive = view.findViewById(R.id.tv_consumption_positive);
            tvConsumptionReserve = view.findViewById(R.id.tv_consumption_reserve);
            tvFlowRate = view.findViewById(R.id.tv_flowRate);
            tvPressure = view.findViewById(R.id.tv_pressure);
            tvValveStatus = view.findViewById(R.id.tv_valveStatus);
            tvMeterStatus = view.findViewById(R.id.tv_meterStatus);
            tvCreateTime = view.findViewById(R.id.tv_createTime);
            tvCommitTime = view.findViewById(R.id.tv_commitTime);
            tvAddress = view.findViewById(R.id.tv_address);
            btnHistoryData = view.findViewById(R.id.btn_historyData);
            btnShowWaterStatistics = view.findViewById(R.id.btn_showWaterStatistics);
            btnShowDataCharts = view.findViewById(R.id.btn_showDataCharts);
            btnOperateMeter = view.findViewById(R.id.btn_operateMeter);
        }
    }
}