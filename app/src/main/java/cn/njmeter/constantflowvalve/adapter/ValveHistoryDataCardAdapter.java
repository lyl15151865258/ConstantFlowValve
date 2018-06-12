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
 * 水表历史数据卡片模式适配器
 *
 * @author LiYuliang
 * @version 2018/1/10
 */

public class ValveHistoryDataCardAdapter extends RecyclerView.Adapter {

    private List<ValveCommitInformation.Data> list;
    private AppCompatActivity appCompatActivity;

    public ValveHistoryDataCardAdapter(AppCompatActivity appCompatActivity, List<ValveCommitInformation.Data> lv) {
        list = lv;
        this.appCompatActivity = appCompatActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_historydata_card, viewGroup, false);
        CardViewHolder cardViewHolder = new CardViewHolder(view);
        cardViewHolder.tvSampleTime = view.findViewById(R.id.tvSampleTime);
        cardViewHolder.tvCommitTime = view.findViewById(R.id.tvCommitTime);
        cardViewHolder.tvValveStatus = view.findViewById(R.id.tvValveStatus);
        cardViewHolder.tvMeterStatus = view.findViewById(R.id.tvMeterStatus);
        cardViewHolder.tvFlow = view.findViewById(R.id.tvFlow);
        cardViewHolder.tvTemperature = view.findViewById(R.id.tvTemperature);
        cardViewHolder.tvFlowRate = view.findViewById(R.id.tvFlowRate);
        cardViewHolder.tvTurns = view.findViewById(R.id.tvTurns);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        CardViewHolder holder = (CardViewHolder) viewHolder;
        ValveCommitInformation.Data data = list.get(position);
        holder.tvSampleTime.setText(data.getSampleTime());
        holder.tvCommitTime.setText(data.getCreateTime());
        holder.tvValveStatus.setText(data.getValveStatus());
        holder.tvMeterStatus.setText(data.getStatus());
        holder.tvFlow.setText(String.format(appCompatActivity.getString(R.string.exampleConsumption), String.valueOf(data.getTotal())));
        holder.tvTemperature.setText(String.format(appCompatActivity.getString(R.string.example_temperature), String.valueOf(data.getT2Inp())));
        holder.tvFlowRate.setText(data.getRotateNum() + "L/h");
        holder.tvTurns.setText(String.valueOf(data.getRotateNum()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class CardViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSampleTime, tvCommitTime, tvValveStatus, tvMeterStatus, tvFlow, tvTemperature, tvFlowRate, tvTurns;

        private CardViewHolder(View itemView) {
            super(itemView);
        }
    }

}
