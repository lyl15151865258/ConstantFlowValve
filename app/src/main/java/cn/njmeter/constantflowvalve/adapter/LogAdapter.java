package cn.njmeter.constantflowvalve.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.bean.Log;

/**
 * 日志显示的适配器
 * Created by LiYuliang on 2018/2/7.
 *
 * @author LiYuliang
 * @version 2017/2/7
 */

public class LogAdapter extends BaseAdapter {

    private List<Log> list;
    private Context context;

    public LogAdapter(Context c, List<Log> lv) {
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
        LogAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_log, parent, false);
            viewHolder = new LogAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LogAdapter.ViewHolder) convertView.getTag();
        }
        Log log = list.get(position);
        viewHolder.tvTime.setText(log.getTime());
        viewHolder.tvContent.setText(log.getContent());
        return convertView;
    }


    private class ViewHolder {
        private TextView tvTime, tvContent;

        private ViewHolder(View view) {
            tvTime = view.findViewById(R.id.tv_time);
            tvContent = view.findViewById(R.id.tv_content);
        }
    }
}
