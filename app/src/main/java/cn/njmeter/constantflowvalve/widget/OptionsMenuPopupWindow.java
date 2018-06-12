package cn.njmeter.constantflowvalve.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.bean.ToolBarMenu;

/**
 * 选项菜单
 * Created at 2018/6/5 0005 9:07
 *
 * @author LiYuliang
 * @version 1.0
 */

public class OptionsMenuPopupWindow extends PopupWindow {

    private Context mContext;

    // Item菜单点击
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public OptionsMenuPopupWindow(Context context, ArrayList<ToolBarMenu> menus) {
        super(context, null);
        this.mContext = context;
        View menuView = LayoutInflater.from(context).inflate(R.layout.popup_layout_toolbar_menu, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(menuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setAnimationStyle(R.style.PopupWindowAnimation);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 这个是为了点击"返回Back"也能使其消失，并且并不会影响你的背景
        this.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.black_translucent)));
        ListView listView = menuView.findViewById(R.id.listView);
        ToolBarMenuAdapter menuAdapter = new ToolBarMenuAdapter(menus);
        listView.setAdapter(menuAdapter);
        menuView.findViewById(R.id.popupBg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsMenuPopupWindow.this.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) mListener.onItemClick(parent, view, position, id);
            }
        });
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor, xoff, yoff);
    }

    // TooBar 菜单
    class ToolBarMenuAdapter extends BaseAdapter {

        private ArrayList<ToolBarMenu> mMenus;

        ToolBarMenuAdapter(ArrayList<ToolBarMenu> menus) {
            this.mMenus = menus;
        }

        @Override
        public int getCount() {
            return mMenus == null ? 0 : mMenus.size();
        }

        @Override
        public ToolBarMenu getItem(int position) {
            return (mMenus == null || mMenus.isEmpty()) ? null : mMenus.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ToolBarMenu item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_of_menu_layout, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (item.count > 0) {
                holder.badgeView.setText((item.count > 99 ? "..." : String.valueOf(item.count)));
                holder.badgeView.setVisibility(View.VISIBLE);
            } else {
                holder.badgeView.setVisibility(View.GONE);
            }
            holder.icon.setImageResource(item.img);
            holder.tvName.setText(item.name);
            return convertView;
        }
    }

    class ViewHolder {
        ImageView icon;
        TextView tvName, badgeView;

        ViewHolder(View convertView) {
            icon = convertView.findViewById(R.id.icon);
            tvName = convertView.findViewById(R.id.tvName);
            badgeView = convertView.findViewById(R.id.badgeView);
            convertView.setTag(this);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }

}
