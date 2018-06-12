package cn.njmeter.constantflowvalve.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.List;

import cn.njmeter.constantflowvalve.R;

/**
 * GridView按钮的适配器
 * Created at 2018/5/18 0018 13:34
 *
 * @author LiYuliang
 * @version 1.0
 */

public class ButtonAdapter extends BaseAdapter {

    private List<String> list;
    private Context context;

    public ButtonAdapter(Context c, List<String> lv) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_button_gridview, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String buttonName = list.get(position);
        viewHolder.btnOperateMeter.setText(buttonName);
        return convertView;
    }

    private class ViewHolder {
        private Button btnOperateMeter;

        private ViewHolder(View view) {
            btnOperateMeter = view.findViewById(R.id.btn_operateMeter);
        }
    }
}
