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
 * 恒流阀标定
 * Created at 2018/6/12 0012 10:23
 *
 * @author LiYuliang
 * @version 1.0
 */

public class DemarcateConstantFlowValveDialog extends Dialog {
    private Context context;
    private OnDialogClickListener dialogClickListener;

    public DemarcateConstantFlowValveDialog(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    //初始化View
    private void initView() {
        setContentView(R.layout.dialog_input_flow_point_constant_flow_valve);
        initWindow();
        findViewById(R.id.btn_demarcate1).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onDemarcate1Click();
            }
        });
        findViewById(R.id.btn_demarcate2).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onDemarcate2Click();
            }
        });
        findViewById(R.id.btn_demarcate3).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onDemarcate3Click();
            }
        });
        findViewById(R.id.btn_demarcate4).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onDemarcate4Click();
            }
        });
        findViewById(R.id.btn_demarcate5).setOnClickListener((v) -> {
            if (dialogClickListener != null) {
                dialogClickListener.onDemarcate5Click();
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
        void onDemarcate1Click();

        void onDemarcate2Click();

        void onDemarcate3Click();

        void onDemarcate4Click();

        void onDemarcate5Click();
    }
}

