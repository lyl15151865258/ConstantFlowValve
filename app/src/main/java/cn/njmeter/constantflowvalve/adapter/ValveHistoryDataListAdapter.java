package cn.njmeter.constantflowvalve.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.bean.ValveCommitInformation;

/**
 * Created by LiYuliang on 2018/1/10 0010.
 * 水表历史数据列表模式适配器
 *
 * @author LiYuliang
 * @version 2018/1/10
 */

public class ValveHistoryDataListAdapter extends RecyclerView.Adapter {

    private List<ValveCommitInformation.Data> list;
    private AppCompatActivity appCompatActivity;

    public ValveHistoryDataListAdapter(AppCompatActivity appCompatActivity, List<ValveCommitInformation.Data> lv) {
        list = lv;
        this.appCompatActivity = appCompatActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_historydata_list, viewGroup, false);
        ListViewHolder listViewHolder = new ListViewHolder(view);
        listViewHolder.tvTime = view.findViewById(R.id.tvTime);
        listViewHolder.tvTurns = view.findViewById(R.id.tvTurns);
        listViewHolder.tvFlowRate = view.findViewById(R.id.tvFlowRate);
        listViewHolder.tvFlow = view.findViewById(R.id.tvFlow);
        return listViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ListViewHolder holder = (ListViewHolder) viewHolder;
        ValveCommitInformation.Data data = list.get(position);
        holder.tvTime.setText(data.getSampleTime());
        holder.tvTurns.setText(String.valueOf(data.getRotateNum()));
        holder.tvFlowRate.setText(data.getFlowPoint() + "L/h");
        holder.tvFlow.setText(String.format(appCompatActivity.getString(R.string.exampleConsumption), String.valueOf(data.getTotal())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTime, tvTurns, tvFlowRate, tvFlow;

        private ListViewHolder(View itemView) {
            super(itemView);
        }
    }

}
