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
 * 修改恒流阀流速
 * Created at 2018/5/24 0024 22:10
 *
 * @author LiYuliang
 * @version 1.0
 */

public class ChangeConstantFlowValveFlowDialog extends Dialog {
    private Context context;
    private OnDialogClickListener dialogClickListener;

    public ChangeConstantFlowValveFlowDialog(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    //初始化View
    private void initView() {
        setContentView(R.layout.dialog_input_flow_constant_flow_valve);
        initWindow();
        findViewById(R.id.btn_setFlow).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onSetFlowClick();
            }
        });
        findViewById(R.id.btn_setAddFlow).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onAddFlowClick();
            }
        });
        findViewById(R.id.btn_setReduceFlow).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onReduceFlowClick();
            }
        });
    }

    /**
     * 添加黑色半透明背景
     */
    private void initWindow() {
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
    }

    public void setOnDialogClickListener(OnDialogClickListener clickListener) {
        dialogClickListener = clickListener;
    }

    /**
     * 添加按钮点击事件
     */
    public interface OnDialogClickListener {
        void onSetFlowClick();

        void onAddFlowClick();

        void onReduceFlowClick();
    }
}

