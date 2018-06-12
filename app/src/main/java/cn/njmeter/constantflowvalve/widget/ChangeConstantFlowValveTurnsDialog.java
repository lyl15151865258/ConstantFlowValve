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
 * 修改恒流阀圈数
 * Created at 2018/6/12 0012 9:41
 *
 * @author LiYuliang
 * @version 1.0
 */

public class ChangeConstantFlowValveTurnsDialog extends Dialog {
    private Context context;
    private OnDialogClickListener dialogClickListener;

    public ChangeConstantFlowValveTurnsDialog(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    //初始化View
    private void initView() {
        setContentView(R.layout.dialog_input_turns_constant_flow_valve);
        initWindow();
        findViewById(R.id.btn_setTurns).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onSetTurnsClick();
            }
        });
        findViewById(R.id.btn_setAddTurns).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onAddTurnsClick();
            }
        });
        findViewById(R.id.btn_setReduceTurns).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onReduceTurnsClick();
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
        void onSetTurnsClick();

        void onAddTurnsClick();

        void onReduceTurnsClick();
    }
}

