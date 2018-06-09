package cn.njmeter.constantflowvalve.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import cn.njmeter.constantflowvalve.R;

/**
 * 水表查询设置查询口径范围
 * Created by LiYuliang on 2018/1/02 0002.
 *
 * @author LiYuliang
 * @version 2018/01/02
 */

public class ChooseMeterSizeDialog extends Dialog {

    private Context context;
    private OnDialogClickListener dialogClickListener;

    public ChooseMeterSizeDialog(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        setContentView(R.layout.dialog_choose_meter_size);
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setBackgroundDrawable(new ColorDrawable(0));//设置window背景
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//设置输入法显示模式
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = context.getResources().getDisplayMetrics();//获取屏幕尺寸
            lp.width = (int) (d.widthPixels * 0.9); //宽度为屏幕80%
            lp.gravity = Gravity.CENTER;  //中央居中
            dialogWindow.setAttributes(lp);
        }
        findViewById(R.id.btn_ok).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onOKClick();
            }
        });
    }

    public void setOnDialogClickListener(OnDialogClickListener clickListener) {
        dialogClickListener = clickListener;
    }

    /**
     * 添加按钮点击事件
     */
    public interface OnDialogClickListener {
        void onOKClick();
    }
}

