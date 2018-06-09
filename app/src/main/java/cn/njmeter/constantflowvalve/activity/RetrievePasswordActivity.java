package cn.njmeter.constantflowvalve.activity;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.bean.NormalResult;
import cn.njmeter.constantflowvalve.constant.Constants;
import cn.njmeter.constantflowvalve.network.ExceptionHandle;
import cn.njmeter.constantflowvalve.network.NetClient;
import cn.njmeter.constantflowvalve.network.NetworkSubscriber;
import cn.njmeter.constantflowvalve.utils.ActivityController;
import cn.njmeter.constantflowvalve.utils.LogUtils;
import cn.njmeter.constantflowvalve.utils.NetworkUtil;
import cn.njmeter.constantflowvalve.utils.RegexUtils;
import cn.njmeter.constantflowvalve.utils.SharedPreferencesUtils;
import cn.njmeter.constantflowvalve.utils.ViewUtils;
import cn.njmeter.constantflowvalve.widget.MyToolbar;
import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 找回主账号密码
 * Created by LiYuliang on 2017/07/10 0010.
 *
 * @author LiYuliang
 * @version 2017/10/20
 */

public class RetrievePasswordActivity extends BaseActivity {

    private Context mContext;
    private EditText etPhoneNumberRetrievePassword, etSmsCode, etNewPassword1, etNewPassword2;
    private TextView tvGetConfirmCode;
    private ImageView ivShowNewPassword1, ivShowNewPassword2;
    private Button btnModify;
    private MyHandler myHandler = new MyHandler(this);
    private Boolean isInvisibleNewPassword1, isInvisibleNewPassword2;
    private EventHandler eventHandler;
    private static final int COUNT_DOWN = -9, COUNT_DOWN_OVER = -8;
    /**
     * 数字正则匹配表达式
     */
    private Pattern continuousNumberPattern = Pattern.compile("[0-9]+");
    /**
     * 短信验证码重复发送倒计时
     */
    private int i = 30;
    /**
     * 短信
     */
    private SmsContent content;
    /**
     * 线程池
     */
    public static ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);
        mContext = this;
        executorService = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(), 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), (r) -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        MyToolbar toolbar = findViewById(R.id.set_password_toolbar);
        toolbar.initToolBar(this, toolbar, getString(R.string.RetrievePassword), R.drawable.back_white, onClickListener);
        content = new SmsContent(new Handler(), this);
        //注册短信变化监听
        getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content);
        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                myHandler.sendMessage(msg);
            }
        };
        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
        ImageView ivDeletePhoneNumber = findViewById(R.id.iv_delete_phoneNumber);
        ivDeletePhoneNumber.setOnClickListener(onClickListener);
        btnModify = findViewById(R.id.btn_modify);
        btnModify.setOnClickListener(onClickListener);
        etPhoneNumberRetrievePassword = findViewById(R.id.et_phoneNumber_retrieve_password);
        etSmsCode = findViewById(R.id.et_sms_code);
        etNewPassword1 = findViewById(R.id.et_newPassword1);
        etNewPassword2 = findViewById(R.id.et_newPassword2);
        etPhoneNumberRetrievePassword.addTextChangedListener(textWatcher);
        etSmsCode.addTextChangedListener(textWatcher);
        etNewPassword1.addTextChangedListener(textWatcher);
        etNewPassword2.addTextChangedListener(textWatcher);
        ivShowNewPassword1 = findViewById(R.id.iv_showNewPassword1);
        ivShowNewPassword2 = findViewById(R.id.iv_showNewPassword2);
        ivShowNewPassword1.setOnClickListener(onClickListener);
        ivShowNewPassword2.setOnClickListener(onClickListener);
        tvGetConfirmCode = findViewById(R.id.tv_getConfirmCode);
        TextView tvNoSmsCode = findViewById(R.id.tv_noSmsCode);
        tvGetConfirmCode.setOnClickListener(onClickListener);
        tvNoSmsCode.setOnClickListener(onClickListener);
        isInvisibleNewPassword1 = true;
        isInvisibleNewPassword2 = true;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (TextUtils.isEmpty(etPhoneNumberRetrievePassword.getText().toString()) || TextUtils.isEmpty(etSmsCode.getText().toString()) ||
                    TextUtils.isEmpty(etNewPassword1.getText().toString()) || TextUtils.isEmpty(etNewPassword2.getText().toString())) {
                btnModify.setEnabled(false);
            } else {
                btnModify.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String phoneNumber = etPhoneNumberRetrievePassword.getText().toString().trim();
            String newPassword1 = etNewPassword1.getText().toString().trim();
            String newPassword2 = etNewPassword2.getText().toString().trim();
            switch (v.getId()) {
                case R.id.iv_left:
                    ActivityController.finishActivity(RetrievePasswordActivity.this);
                    break;
                case R.id.iv_delete_phoneNumber:
                    etPhoneNumberRetrievePassword.setText("");
                    break;
                case R.id.btn_modify:
                    int passwordLength = 6;
                    if (!newPassword1.equals(newPassword2)) {
                        showToast("两次输入的密码不一致");
                        return;
                    } else if (newPassword1.length() < passwordLength) {
                        showToast("新密码长度小于6位");
                        return;
                    }
                    //校验手机验证码
                    confirmCode();
                    break;
                case R.id.iv_showNewPassword1:
                    ViewUtils.changePasswordState(isInvisibleNewPassword1, etNewPassword1, ivShowNewPassword1);
                    isInvisibleNewPassword1 = !isInvisibleNewPassword1;
                    break;
                case R.id.iv_showNewPassword2:
                    ViewUtils.changePasswordState(isInvisibleNewPassword2, etNewPassword2, ivShowNewPassword2);
                    isInvisibleNewPassword2 = !isInvisibleNewPassword2;
                    break;
                case R.id.tv_getConfirmCode:
                    //获取短信验证码
                    if (TextUtils.isEmpty(phoneNumber)) {
                        showToast("请输入手机号");
                        return;
                    }
                    if (!RegexUtils.checkMobile(phoneNumber)) {
                        showToast("请输入正确的手机号");
                        return;
                    }
                    SMSSDK.getVerificationCode("86", phoneNumber, onSendMessageHandler);
                    tvGetConfirmCode.setClickable(false);
                    tvGetConfirmCode.setText(String.format(getString(R.string.resend_info_time), i));
                    Runnable runnable = () -> {
                        for (; i > 0; i--) {
                            myHandler.sendEmptyMessage(COUNT_DOWN);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        myHandler.sendEmptyMessage(COUNT_DOWN_OVER);
                    };
                    executorService.submit(runnable);
                    break;
                case R.id.tv_noSmsCode:
                    //收不到短信验证码
                    openActivity(ContactUsActivity.class);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 验证手机验证码
     */
    private void confirmCode() {
        String phoneNumber = etPhoneNumberRetrievePassword.getText().toString();
        String smsCode = etSmsCode.getText().toString();
        SMSSDK.submitVerificationCode("86", phoneNumber, smsCode);
    }

    /**
     * 是否接收短信的验证
     */
    private OnSendMessageHandler onSendMessageHandler = (s, s1) -> {
        //此方法在发送验证短信前被调用，传入参数为接收者号码,返回true表示此号码无须实际接收短信
        return false;
    };

    /**
     * 手机短信观察者
     */
    private class SmsContent extends ContentObserver {
        private Cursor cursor = null;
        private Context context;

        private SmsContent(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            LogUtils.d("SMSTest", "Begin");
            //读取收件箱中指定号码的短信
//            cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"_id", "address", "read", "body"},
//                    " address=? and read=?", new String[]{"10086", "0"}, "_id desc");//按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
            cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"_id", "address", "read", "body"},
                    null, null, "_id desc");
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int smsBodyColumn = cursor.getColumnIndex("body");
                String smsBody = cursor.getString(smsBodyColumn);
                LogUtils.d("SMSTest", "smsBody = " + smsBody);
                etSmsCode.setText(getDynamicPassword(smsBody));
                LogUtils.d("SMSTest", "cursor.isBeforeFirst(): " + cursor.isBeforeFirst() + " cursor.getCount(): " + cursor.getCount());
                cursor.close();
            }
        }

        private String getDynamicPassword(String str) {
            Matcher m = continuousNumberPattern.matcher(str);
            String dynamicPassword = "";
            while (m.find()) {
                if (m.group().length() == 4 || m.group().length() == 6) {
                    System.out.print(m.group());
                    dynamicPassword = m.group();
                }
            }
            return dynamicPassword;
        }
    }

    /**
     * 重置密码
     *
     * @param phoneNumber 手机号
     * @param newPassword 密码
     */
    private void resetPassword(String phoneNumber, String newPassword) {
        Map<String, String> params = new HashMap<>(2);
        params.put("loginName", phoneNumber);
        params.put("password", newPassword);
        Observable<NormalResult> normalResultObservable = NetClient.getInstances(NetClient.BASE_URL_PROJECT).getNjMeterApi().resetPassword(params);
        normalResultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<NormalResult>(mContext, getClass().getSimpleName()) {

            @Override
            public void onStart() {
                super.onStart();
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast("当前网络不可用，请检查网络");
                    if (!isUnsubscribed()) {
                        unsubscribe();
                    }
                } else {
                    showLoadingDialog(mContext, "重置中", true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast(responseThrowable.message);
            }

            @Override
            public void onNext(NormalResult normalResult) {
                cancelDialog();
                if (normalResult == null) {
                    showToast("重置失败，返回值异常");
                } else {
                    String result = normalResult.getResult();
                    String message = normalResult.getMessage();
                    switch (result) {
                        case Constants.SUCCESS:
                            showToast("密码重置成功");
                            SharedPreferencesUtils.getInstance().saveData("userName_main", etPhoneNumberRetrievePassword.getText().toString());
                            SharedPreferencesUtils.getInstance().saveData("passWord_main", etNewPassword1.getText().toString());
                            ActivityController.finishActivity(RetrievePasswordActivity.this);
                            break;
                        case Constants.FAIL:
                            showToast("密码重置失败，" + message);
                            break;
                        default:
                            showToast("密码重置失败");
                            break;
                    }
                }
            }
        });
    }

    private static class MyHandler extends Handler {
        WeakReference<RetrievePasswordActivity> mActivity;

        MyHandler(RetrievePasswordActivity retrievePasswordActivity) {
            mActivity = new WeakReference<>(retrievePasswordActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            RetrievePasswordActivity theActivity = mActivity.get();
            if (msg.what == COUNT_DOWN) {
                theActivity.tvGetConfirmCode.setText(String.format(theActivity.getString(R.string.resend_info_time), theActivity.i));
            } else if (msg.what == COUNT_DOWN_OVER) {
                theActivity.tvGetConfirmCode.setText("获取验证码");
                theActivity.tvGetConfirmCode.setClickable(true);
                theActivity.i = 30;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        //验证码验证成功
                        //由于登陆注册页面也有短信监听，所以需要先判断栈顶的Activity是否是当前Activity对象
                        AppCompatActivity currentActivity = (AppCompatActivity) ActivityController.getInstance().getCurrentActivity();
                        if (currentActivity instanceof RetrievePasswordActivity) {
                            //验证码验证成功重置密码
                            String phoneNumber = theActivity.etPhoneNumberRetrievePassword.getText().toString().trim();
                            String newPassword = theActivity.etNewPassword1.getText().toString().trim();
                            theActivity.resetPassword(phoneNumber, newPassword);
                        }
                    } else if (result == SMSSDK.RESULT_ERROR) {
                        theActivity.showToast("验证码输入错误");
                    }
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        theActivity.showToast("验证码已经发送");
                    } else if (result == SMSSDK.RESULT_ERROR) {
                        theActivity.showToast("验证码发送失败");
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
        SMSSDK.unregisterEventHandler(eventHandler);
        getContentResolver().unregisterContentObserver(content);
        executorService.shutdown();
    }
}
