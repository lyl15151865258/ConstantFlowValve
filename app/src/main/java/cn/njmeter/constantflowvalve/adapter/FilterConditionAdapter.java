package cn.njmeter.constantflowvalve.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.bean.FilterCondition;

/**
 * Created by LiYuliang on 2018/1/3 0003.
 * 筛选条件显示的适配器
 *
 * @author LiYuliang
 * @version 2018/1/3
 */

public class FilterConditionAdapter extends BaseAdapter {

    private List<FilterCondition> list;
    private Context context;

    public FilterConditionAdapter(Context c, List<FilterCondition> lv) {
        context = c;
        list = lv;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_filter, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FilterCondition filterCondition = list.get(position);
        viewHolder.tvNumber.setText(String.valueOf(position + 1) + "、");
        viewHolder.tvContentFiltering.setText(filterCondition.getContentFiltering() + filterCondition.getComparisonOperators() + filterCondition.getValueFiltering());
        return convertView;
    }


    private class ViewHolder {

        private TextView tvContentFiltering, tvNumber;

        private ViewHolder(View view) {
            tvContentFiltering = view.findViewById(R.id.tv_content_filtering);
            tvNumber = view.findViewById(R.id.tv_number);
        }
    }
}
