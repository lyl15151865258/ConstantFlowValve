package cn.njmeter.constantflowvalve.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * 可嵌套于ScrollView布局中的ListView
 * Created at 2018/6/5 0005 8:37
 *
 * @author LiYuliang
 * @version 1.0
 */

public class ListViewAdaptive extends ListView {

    public ListViewAdaptive(Context context) {
        super(context);
    }

    public ListViewAdaptive(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewAdaptive(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // 重写该方法，达到使ListView适应ScrollView的效果
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMaxWidthOfChildren() + getPaddingLeft() + getPaddingRight();//计算ListView的宽度
        int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), height);
    }

    // 计算ListView每个item的宽度，取最长长度作为ListView的宽度
    private int getMaxWidthOfChildren() {
        int maxWidth = 0;
        View view = null;
        int count = getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            view = getAdapter().getView(i, view, this);
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            if (view.getMeasuredWidth() > maxWidth) {
                maxWidth = view.getMeasuredWidth();
            }
        }
        return maxWidth;
    }
}
