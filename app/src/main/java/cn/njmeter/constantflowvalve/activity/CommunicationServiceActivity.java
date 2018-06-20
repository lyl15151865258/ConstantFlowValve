package cn.njmeter.constantflowvalve.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.njmeter.constantflowvalve.ConstantFlowValveApplication;
import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.adapter.ButtonAdapter;
import cn.njmeter.constantflowvalve.adapter.LogAdapter;
import cn.njmeter.constantflowvalve.bean.ProductProtocol;
import cn.njmeter.constantflowvalve.bean.ConstantFlowValveCmd;
import cn.njmeter.constantflowvalve.bean.EventMsg;
import cn.njmeter.constantflowvalve.bean.Log;
import cn.njmeter.constantflowvalve.bean.MBUS;
import cn.njmeter.constantflowvalve.bean.ParameterProtocol;
import cn.njmeter.constantflowvalve.bean.SSumHeat;
import cn.njmeter.constantflowvalve.constant.Constants;
import cn.njmeter.constantflowvalve.constant.NetWork;
import cn.njmeter.constantflowvalve.constant.ProductType;
import cn.njmeter.constantflowvalve.constant.SocketConstant;
import cn.njmeter.constantflowvalve.network.bean.SocketPackage;
import cn.njmeter.constantflowvalve.service.SocketService;
import cn.njmeter.constantflowvalve.utils.ActivityController;
import cn.njmeter.constantflowvalve.utils.DataAnalysisUtils;
import cn.njmeter.constantflowvalve.utils.GsonUtils;
import cn.njmeter.constantflowvalve.utils.MathUtils;
import cn.njmeter.constantflowvalve.utils.StringUtils;
import cn.njmeter.constantflowvalve.utils.TimeUtils;
import cn.njmeter.constantflowvalve.widget.ChangeConstantFlowValveFlowDialog;
import cn.njmeter.constantflowvalve.widget.ChangeConstantFlowValveSleepMonthDialog;
import cn.njmeter.constantflowvalve.widget.ChangeConstantFlowValveTurnsDialog;
import cn.njmeter.constantflowvalve.widget.ChangeConstantFlowValveUploadIntervalDialog;
import cn.njmeter.constantflowvalve.widget.DemarcateConstantFlowValveDialog;
import cn.njmeter.constantflowvalve.widget.MyToolbar;

/**
 * 供热平台通讯服务页面
 * Created at 2018/5/24 0024 10:41
 *
 * @author LiYuliang
 * @version 1.0
 */

public class CommunicationServiceActivity extends BaseActivity {

    private Context mContext;
    private EditText imeiEdtTxt, meterIdEdtTxt;
    private TextView meterIdTv, totalConsumptionTv, tvQuanShu, tvSleepTime, tvUploadInterval, tvTemperature,
            flowRateTv, valveStatusTv, meterStatusTv, batteryVoltageTv, meterTimeTv;
    private List<Log> logList;
    private LogAdapter logAdapter;
    private ExecutorService executorService;

    private ServiceConnection serviceConnection;
    public SocketService socketService;
    /**
     * Socket连接状态标记，用于发送消息时的判断
     */
    private boolean isConnectSuccess = false;

    private String loginId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.initToolBar(this, toolbar, "衡流阀操作", R.drawable.back_white, onClickListener);
        initView();
        executorService = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), (r) -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        String serverHost = ConstantFlowValveApplication.getInstance().getAccount().getServer_Host_XHS();
        String socketPort = ConstantFlowValveApplication.getInstance().getAccount().getSocket_Port_XHS();

        loginId = String.valueOf(ConstantFlowValveApplication.getInstance().getAccount().getLoginId());
        String meterId = getIntent().getStringExtra("meterId");
        String imei = getIntent().getStringExtra("imei");
        meterIdEdtTxt.setText(meterId);
        imeiEdtTxt.setText(imei);

        //启动service
        Intent intent = new Intent(this, SocketService.class);
        intent.putExtra("ip", serverHost);
        intent.putExtra("port", socketPort);
        intent.putExtra("loginId", loginId);
        startService(intent);
    }

    private void initView() {
        imeiEdtTxt = findViewById(R.id.et_imei_collector);
        meterIdEdtTxt = findViewById(R.id.et_id_meter);
        meterIdTv = findViewById(R.id.et_meterId);
        totalConsumptionTv = findViewById(R.id.tvTotalFlow);
        tvQuanShu = findViewById(R.id.tvQuanShu);
        tvSleepTime = findViewById(R.id.tvSleepTime);
        tvUploadInterval = findViewById(R.id.tvUploadInterval);
        tvTemperature = findViewById(R.id.tvTemperature);
        flowRateTv = findViewById(R.id.tv_flowRate);
        valveStatusTv = findViewById(R.id.tv_valve_status);
        meterStatusTv = findViewById(R.id.tv_meterStatus);
        batteryVoltageTv = findViewById(R.id.tv_battery_voltage);
        meterTimeTv = findViewById(R.id.tv_meterTime);
        ImageView deleteImeiIv = findViewById(R.id.iv_deleteImei);
        ImageView deleteMeterIdIv = findViewById(R.id.iv_deleteDeviceId);
        deleteImeiIv.setOnClickListener(onClickListener);
        deleteMeterIdIv.setOnClickListener(onClickListener);

        ListView lvLog = findViewById(R.id.lv_log);
        logList = new ArrayList<>();
        logAdapter = new LogAdapter(mContext, logList);
        lvLog.setAdapter(logAdapter);

        GridView myGridViewButton = findViewById(R.id.myGridView_button);
        List<String> buttonNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.button_operate_constant_flow_valve)));
        ButtonAdapter buttonAdapter = new ButtonAdapter(mContext, buttonNames);
        myGridViewButton.setAdapter(buttonAdapter);
        myGridViewButton.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 收到EventBus发来的消息并处理
     *
     * @param msg 消息对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(EventMsg msg) {
        if (msg.getTag().equals(Constants.CONNECT_SUCCESS_SOCKET)) {
            //接收到这个消息说明连接成功
            isConnectSuccess = true;
            refreshLogList("已连接到服务器，通信正常");
            bindSocketService();
        }
        if (msg.getTag().equals(Constants.CONNECT_FAIL_SOCKET)) {
            //接收到这个消息说明连接失败或者中断了
            isConnectSuccess = false;
            refreshLogList("与服务器断开连接，通信异常");
            bindSocketService();
        }
        if (msg.getTag().equals(Constants.SHOW_TOAST_SOCKET)) {
            //接收到这个消息说明需要显示一个Toast
            showToast(msg.getMsg());
        }
        if (msg.getTag().equals(Constants.SHOW_DATA_SOCKET)) {
            //接收到这个消息说明需要处理数据
            String message = msg.getMsg();
            ProductProtocol productProtocol = new ProductProtocol();
            MBUS mbus = new MBUS();
            SSumHeat ssumheat = new SSumHeat();
            ParameterProtocol parameterprotocol = new ParameterProtocol();
            switch (DataAnalysisUtils.txIsLegal(message, productProtocol, ssumheat, mbus, parameterprotocol)) {
                case ProductType.CONSTANT_FLOW_VALVE_READ_PARAMETER:
                    //抄表
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "读取数据成功");
                    showToast("恒流阀" + productProtocol.getMeterID() + "读取数据成功");
                    meterIdTv.setText(productProtocol.getMeterID());
                    totalConsumptionTv.setText(MathUtils.getOriginNumber(productProtocol.getTotal()) + productProtocol.getTotalUnit());
                    flowRateTv.setText(StringUtils.getPrettyNumber(productProtocol.getFlowRate()) + "L/h");
                    tvTemperature.setText(String.format(getString(R.string.example_temperature), String.valueOf(productProtocol.getT2InP())));

                    tvQuanShu.setText(String.valueOf(productProtocol.getQuanShu()));
                    tvSleepTime.setText(productProtocol.getStartSleep() + "月~" + productProtocol.getStopSleep() + "月");
                    tvUploadInterval.setText(String.valueOf(productProtocol.getUploadInterval()) + "小时");

                    valveStatusTv.setText(productProtocol.getValveStatus());
                    meterStatusTv.setText(productProtocol.getStatus());
                    batteryVoltageTv.setText(productProtocol.getVol() + "V");
                    meterTimeTv.setText(productProtocol.getTimeInP());
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_TURNS:
                    //指定圈数
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "指定" + productProtocol.getQuanShu() + "圈设置成功");
                    showToast("恒流阀" + productProtocol.getMeterID() + "指定圈数成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_ADD_TURNS:
                    //增大圈数
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "增大" + productProtocol.getQuanShu() + "圈设置成功");
                    showToast("恒流阀" + productProtocol.getMeterID() + "增大圈数成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_REDUCE_TURNS:
                    //减小圈数
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "减小" + productProtocol.getQuanShu() + "圈设置成功");
                    showToast("恒流阀" + productProtocol.getMeterID() + "减小圈数成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_FLOW:
                    //指定流速
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "指定流速" + StringUtils.getPrettyNumber(productProtocol.getFlowRate()) + "L/h设置成功");
                    showToast("恒流阀" + productProtocol.getMeterID() + "指定流速成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_ADD_FLOW:
                    //增大流速
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "增大流速" + StringUtils.getPrettyNumber(productProtocol.getFlowRate()) + "L/h设置成功");
                    showToast("恒流阀" + productProtocol.getMeterID() + "增大流速成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_REDUCE_FLOW:
                    //减小流速
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "减小流速" + StringUtils.getPrettyNumber(productProtocol.getFlowRate()) + "L/h设置成功");
                    showToast("恒流阀" + productProtocol.getMeterID() + "减小流速成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_ZEROING:
                    //流量点、流速回零
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "流量点、流速回零设置成功");
                    showToast("恒流阀" + productProtocol.getMeterID() + "回零设置成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_DEMARCATE1:
                    //流量点1标定
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "流量点1标定成功，圈数：" + productProtocol.getQuanShu() + "，流速：" + StringUtils.getPrettyNumber(productProtocol.getFlowRate()) + "L/h");
                    showToast("恒流阀" + productProtocol.getMeterID() + "流量点1标定成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_DEMARCATE2:
                    //流量点2标定
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "流量点2标定成功，圈数：" + productProtocol.getQuanShu() + "，流速：" + StringUtils.getPrettyNumber(productProtocol.getFlowRate()) + "L/h");
                    showToast("恒流阀" + productProtocol.getMeterID() + "流量点2标定成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_DEMARCATE3:
                    //流量点3标定
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "流量点3标定成功，圈数：" + productProtocol.getQuanShu() + "，流速：" + StringUtils.getPrettyNumber(productProtocol.getFlowRate()) + "L/h");
                    showToast("恒流阀" + productProtocol.getMeterID() + "流量点3标定成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_DEMARCATE4:
                    //流量点4标定
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "流量点4标定成功，圈数：" + productProtocol.getQuanShu() + "，流速：" + StringUtils.getPrettyNumber(productProtocol.getFlowRate()) + "L/h");
                    showToast("恒流阀" + productProtocol.getMeterID() + "流量点4标定成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_DEMARCATE5:
                    //流量点5标定
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "流量点5标定成功，圈数：" + productProtocol.getQuanShu() + "，流速：" + StringUtils.getPrettyNumber(productProtocol.getFlowRate()) + "L/h");
                    showToast("恒流阀" + productProtocol.getMeterID() + "流量点5标定成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_SLEEP_MONTHS:
                    //设置休眠月份
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "设置休眠月份：" + StringUtils.getPrettyNumber(productProtocol.getFlowRate()) + "~" + productProtocol.getQuanShu() + "月成功");
                    showToast("恒流阀" + productProtocol.getMeterID() + "设置休眠月份成功");
                    break;
                case ProductType.CONSTANT_FLOW_VALVE_UPLOAD_INTERVAL:
                    //设置上传间隔
                    refreshLogList("恒流阀" + productProtocol.getMeterID() + "设置上传间隔：" + StringUtils.getPrettyNumber(productProtocol.getFlowRate()) + "小时成功");
                    showToast("恒流阀" + productProtocol.getMeterID() + "设置上传间隔成功");
                    break;
                default:
                    String text2;
                    switch (message) {
                        case SocketConstant.DEVICE_IS_NOT_EXIST:
                            text2 = "不存在该采集器";
                            break;
                        case SocketConstant.DEVICE_IS_OFFLINE:
                            text2 = "该采集器不在线";
                            break;
                        default:
                            text2 = msg.getMsg();
                            break;
                    }
                    refreshLogList(text2);
                    showToast(text2);
                    break;
            }
        }
    }

    private void bindSocketService() {
        /*通过binder拿到service*/
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                SocketService.SocketBinder binder = (SocketService.SocketBinder) iBinder;
                socketService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent intent = new Intent(mContext, SocketService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(this);
                break;
            case R.id.iv_deleteImei:
                imeiEdtTxt.setText("");
                break;
            case R.id.iv_deleteDeviceId:
                meterIdEdtTxt.setText("");
                break;
            default:
                break;
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String meterId = meterIdEdtTxt.getText().toString();
            String imei = imeiEdtTxt.getText().toString();
            meterIdEdtTxt.clearFocus();
            if (meterId.length() != Constants.METER_ID_LENGTH) {
                showToast("请输入8位设备编号");
                return;
            }
            if (imei.length() != Constants.IMEI_LENGTH) {
                showToast("请输入11位采集器编号");
                return;
            }
            SocketPackage socketPackage = new SocketPackage();
            socketPackage.setUserId(loginId);
            socketPackage.setDeviceType(ProductType.CONSTANT_FLOW_VALVE_INT);
            socketPackage.setImei(imei);
            socketPackage.setMeterId(meterId);
            socketPackage.setSingleOpt(1);
            socketPackage.setUseCmdCode(true);
            ConstantFlowValveCmd constantFlowValveCmd = new ConstantFlowValveCmd();
            switch (position) {
                case 0:
                    //抄表
                    clearTableData();
                    socketPackage.setCmdCode(0);
                    socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                    operateMeterBySocket(socketPackage);
                    refreshLogList("恒流阀" + meterId + "读取阀门数据指令已发送");
                    break;
                case 1:
                    //调整流速
                    showChangeFlowDialog(meterId, socketPackage, constantFlowValveCmd);
                    break;
                case 2:
                    //调整圈数
                    showChangeTurnsDialog(meterId, socketPackage, constantFlowValveCmd);
                    break;
                case 3:
                    //回零
                    socketPackage.setCmdCode(30);
                    socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                    operateMeterBySocket(socketPackage);
                    refreshLogList("恒流阀" + meterId + "回零指令已发送");
                    break;
                case 4:
                    //流量点标定
                    showDemarcateDialog(meterId, socketPackage, constantFlowValveCmd);
                    break;
                case 5:
                    //设置休眠月份
                    showChangeSleepMonthDialog(meterId, socketPackage, constantFlowValveCmd);
                    break;
                case 6:
                    //设置上传间隔
                    showChangeUploadIntervalDialog(meterId, socketPackage, constantFlowValveCmd);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 显示调整流速的弹窗
     *
     * @param meterId              设备编号
     * @param socketPackage        Socket包的实体类对象
     * @param constantFlowValveCmd 恒流阀指令实体类的对象
     */
    private void showChangeFlowDialog(String meterId, SocketPackage socketPackage, ConstantFlowValveCmd constantFlowValveCmd) {
        ChangeConstantFlowValveFlowDialog changeConstantFlowValveFlowDialog = new ChangeConstantFlowValveFlowDialog(mContext);
        changeConstantFlowValveFlowDialog.setCancelable(true);
        EditText etFlow = changeConstantFlowValveFlowDialog.findViewById(R.id.et_flow);
        EditText etAddFlow = changeConstantFlowValveFlowDialog.findViewById(R.id.et_addFlow);
        EditText etReduceFlow = changeConstantFlowValveFlowDialog.findViewById(R.id.et_reduceFlow);
        changeConstantFlowValveFlowDialog.setOnDialogClickListener(new ChangeConstantFlowValveFlowDialog.OnDialogClickListener() {
            @Override
            public void onSetFlowClick() {
                //设置指定流速
                socketPackage.setCmdCode(21);
                String flow = etFlow.getText().toString();
                if (TextUtils.isEmpty(flow)) {
                    showToast("请输入流速值");
                } else {
                    constantFlowValveCmd.setLiuliang(flow);
                    socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                    operateMeterBySocket(socketPackage);
                    refreshLogList("恒流阀" + meterId + "设定指定流速指令已发送");
                    changeConstantFlowValveFlowDialog.dismiss();
                }
            }

            @Override
            public void onAddFlowClick() {
                //增大流速
                socketPackage.setCmdCode(22);
                String flow = etAddFlow.getText().toString();
                if (TextUtils.isEmpty(flow)) {
                    showToast("请输入增大的流速值");
                } else {
                    constantFlowValveCmd.setLiuliang(flow);
                    socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                    operateMeterBySocket(socketPackage);
                    refreshLogList("恒流阀" + meterId + "增大流速指令已发送");
                    changeConstantFlowValveFlowDialog.dismiss();
                }
            }

            @Override
            public void onReduceFlowClick() {
                //减小流速
                socketPackage.setCmdCode(23);
                String flow = etReduceFlow.getText().toString();
                if (TextUtils.isEmpty(flow)) {
                    showToast("请输入减小的流速值");
                } else {
                    constantFlowValveCmd.setLiuliang(flow);
                    socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                    operateMeterBySocket(socketPackage);
                    refreshLogList("恒流阀" + meterId + "减小流速指令已发送");
                    changeConstantFlowValveFlowDialog.dismiss();
                }
            }
        });
        changeConstantFlowValveFlowDialog.show();
    }

    /**
     * 显示调整圈数的弹窗
     *
     * @param meterId              设备编号
     * @param socketPackage        Socket包的实体类对象
     * @param constantFlowValveCmd 恒流阀指令实体类的对象
     */
    private void showChangeTurnsDialog(String meterId, SocketPackage socketPackage, ConstantFlowValveCmd constantFlowValveCmd) {
        ChangeConstantFlowValveTurnsDialog changeConstantFlowValveTurnsDialog = new ChangeConstantFlowValveTurnsDialog(mContext);
        changeConstantFlowValveTurnsDialog.setCancelable(true);
        EditText etTurns = changeConstantFlowValveTurnsDialog.findViewById(R.id.et_turns);
        EditText etAddTurns = changeConstantFlowValveTurnsDialog.findViewById(R.id.et_addTurns);
        EditText etReduceTurns = changeConstantFlowValveTurnsDialog.findViewById(R.id.et_reduceTurns);
        changeConstantFlowValveTurnsDialog.setOnDialogClickListener(new ChangeConstantFlowValveTurnsDialog.OnDialogClickListener() {
            @Override
            public void onSetTurnsClick() {
                //设置指定圈数
                socketPackage.setCmdCode(11);
                String turns = etTurns.getText().toString();
                if (TextUtils.isEmpty(turns)) {
                    showToast("请输入圈数");
                } else {
                    constantFlowValveCmd.setQuanshu(turns);
                    socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                    operateMeterBySocket(socketPackage);
                    refreshLogList("恒流阀" + meterId + "设定指定圈数指令已发送");
                    changeConstantFlowValveTurnsDialog.dismiss();
                }
            }

            @Override
            public void onAddTurnsClick() {
                //增大圈数
                socketPackage.setCmdCode(13);
                String turns = etAddTurns.getText().toString();
                if (TextUtils.isEmpty(turns)) {
                    showToast("请输入增大的流速值");
                } else {
                    constantFlowValveCmd.setQuanshu(turns);
                    socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                    operateMeterBySocket(socketPackage);
                    refreshLogList("恒流阀" + meterId + "增大圈数指令已发送");
                    changeConstantFlowValveTurnsDialog.dismiss();
                }
            }

            @Override
            public void onReduceTurnsClick() {
                //减小圈数
                socketPackage.setCmdCode(12);
                String turns = etReduceTurns.getText().toString();
                if (TextUtils.isEmpty(turns)) {
                    showToast("请输入减小的流速值");
                } else {
                    constantFlowValveCmd.setQuanshu(turns);
                    socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                    operateMeterBySocket(socketPackage);
                    refreshLogList("恒流阀" + meterId + "减小圈数指令已发送");
                    changeConstantFlowValveTurnsDialog.dismiss();
                }
            }
        });
        changeConstantFlowValveTurnsDialog.show();
    }

    /**
     * 显示标定流量点的弹窗
     *
     * @param meterId              设备编号
     * @param socketPackage        Socket包的实体类对象
     * @param constantFlowValveCmd 恒流阀指令实体类的对象
     */
    private void showDemarcateDialog(String meterId, SocketPackage socketPackage, ConstantFlowValveCmd constantFlowValveCmd) {
        DemarcateConstantFlowValveDialog demarcateConstantFlowValveDialog = new DemarcateConstantFlowValveDialog(mContext);
        demarcateConstantFlowValveDialog.setCancelable(true);
        EditText etTurns1 = demarcateConstantFlowValveDialog.findViewById(R.id.et_turns1);
        EditText etFlow1 = demarcateConstantFlowValveDialog.findViewById(R.id.et_flow1);
        EditText etTurns2 = demarcateConstantFlowValveDialog.findViewById(R.id.et_turns2);
        EditText etFlow2 = demarcateConstantFlowValveDialog.findViewById(R.id.et_flow2);
        EditText etTurns3 = demarcateConstantFlowValveDialog.findViewById(R.id.et_turns3);
        EditText etFlow3 = demarcateConstantFlowValveDialog.findViewById(R.id.et_flow3);
        EditText etTurns4 = demarcateConstantFlowValveDialog.findViewById(R.id.et_turns4);
        EditText etFlow4 = demarcateConstantFlowValveDialog.findViewById(R.id.et_flow4);
        EditText etTurns5 = demarcateConstantFlowValveDialog.findViewById(R.id.et_turns5);
        EditText etFlow5 = demarcateConstantFlowValveDialog.findViewById(R.id.et_flow5);
        demarcateConstantFlowValveDialog.setOnDialogClickListener(new DemarcateConstantFlowValveDialog.OnDialogClickListener() {
            @Override
            public void onDemarcate1Click() {
                //标定流量点1
                socketPackage.setCmdCode(41);
                String turns = etTurns1.getText().toString();
                String flow = etFlow1.getText().toString();
                if (TextUtils.isEmpty(turns)) {
                    showToast("请输入圈数");
                    return;
                }
                if (TextUtils.isEmpty(flow)) {
                    showToast("请输入流速");
                    return;
                }
                constantFlowValveCmd.setQuanshu(turns);
                constantFlowValveCmd.setLiuliang(flow);
                socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                operateMeterBySocket(socketPackage);
                refreshLogList("恒流阀" + meterId + "标定流量点1指令已发送");
                demarcateConstantFlowValveDialog.dismiss();
            }

            @Override
            public void onDemarcate2Click() {
                //标定流量点2
                socketPackage.setCmdCode(42);
                String turns = etTurns2.getText().toString();
                String flow = etFlow2.getText().toString();
                if (TextUtils.isEmpty(turns)) {
                    showToast("请输入圈数");
                    return;
                }
                if (TextUtils.isEmpty(flow)) {
                    showToast("请输入流速");
                    return;
                }
                constantFlowValveCmd.setQuanshu(turns);
                constantFlowValveCmd.setLiuliang(flow);
                socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                operateMeterBySocket(socketPackage);
                refreshLogList("恒流阀" + meterId + "标定流量点2指令已发送");
                demarcateConstantFlowValveDialog.dismiss();
            }

            @Override
            public void onDemarcate3Click() {
                //标定流量点3
                socketPackage.setCmdCode(43);
                String turns = etTurns3.getText().toString();
                String flow = etFlow3.getText().toString();
                if (TextUtils.isEmpty(turns)) {
                    showToast("请输入圈数");
                    return;
                }
                if (TextUtils.isEmpty(flow)) {
                    showToast("请输入流速");
                    return;
                }
                constantFlowValveCmd.setQuanshu(turns);
                constantFlowValveCmd.setLiuliang(flow);
                socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                operateMeterBySocket(socketPackage);
                refreshLogList("恒流阀" + meterId + "标定流量点3指令已发送");
                demarcateConstantFlowValveDialog.dismiss();
            }

            @Override
            public void onDemarcate4Click() {
                //标定流量点4
                socketPackage.setCmdCode(44);
                String turns = etTurns4.getText().toString();
                String flow = etFlow4.getText().toString();
                if (TextUtils.isEmpty(turns)) {
                    showToast("请输入圈数");
                    return;
                }
                if (TextUtils.isEmpty(flow)) {
                    showToast("请输入流速");
                    return;
                }
                constantFlowValveCmd.setQuanshu(turns);
                constantFlowValveCmd.setLiuliang(flow);
                socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                operateMeterBySocket(socketPackage);
                refreshLogList("恒流阀" + meterId + "标定流量点4指令已发送");
                demarcateConstantFlowValveDialog.dismiss();
            }

            @Override
            public void onDemarcate5Click() {
                //标定流量点5
                socketPackage.setCmdCode(45);
                String turns = etTurns5.getText().toString();
                String flow = etFlow5.getText().toString();
                if (TextUtils.isEmpty(turns)) {
                    showToast("请输入圈数");
                    return;
                }
                if (TextUtils.isEmpty(flow)) {
                    showToast("请输入流速");
                    return;
                }
                constantFlowValveCmd.setQuanshu(turns);
                constantFlowValveCmd.setLiuliang(flow);
                socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                operateMeterBySocket(socketPackage);
                refreshLogList("恒流阀" + meterId + "标定流量点5指令已发送");
                demarcateConstantFlowValveDialog.dismiss();
            }
        });
        demarcateConstantFlowValveDialog.show();
    }

    /**
     * 显示调整休眠时间的弹窗
     *
     * @param meterId              设备编号
     * @param socketPackage        Socket包的实体类对象
     * @param constantFlowValveCmd 恒流阀指令实体类的对象
     */
    private void showChangeSleepMonthDialog(String meterId, SocketPackage socketPackage, ConstantFlowValveCmd constantFlowValveCmd) {
        ChangeConstantFlowValveSleepMonthDialog changeConstantFlowValveSleepMonthDialog = new ChangeConstantFlowValveSleepMonthDialog(mContext);
        changeConstantFlowValveSleepMonthDialog.setCancelable(true);
        EditText etStartMonth = changeConstantFlowValveSleepMonthDialog.findViewById(R.id.et_startMonth);
        EditText etStopMonth = changeConstantFlowValveSleepMonthDialog.findViewById(R.id.et_stopMonth);
        changeConstantFlowValveSleepMonthDialog.setOnDialogClickListener(new ChangeConstantFlowValveSleepMonthDialog.OnDialogClickListener() {
            @Override
            public void onSetSleepMonthClick() {
                //设置休眠月份
                socketPackage.setCmdCode(50);
                String startMonth = etStartMonth.getText().toString();
                String stopMonth = etStopMonth.getText().toString();
                if (TextUtils.isEmpty(startMonth)) {
                    showToast("请输入休眠开始月份");
                    return;
                }
                if (TextUtils.isEmpty(stopMonth)) {
                    showToast("请输入休眠结束月份");
                    return;
                }
                constantFlowValveCmd.setLiuliang(startMonth);
                constantFlowValveCmd.setQuanshu(stopMonth);
                socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                operateMeterBySocket(socketPackage);
                refreshLogList("恒流阀" + meterId + "设置休眠月份指令已发送");
                changeConstantFlowValveSleepMonthDialog.dismiss();
            }
        });
        changeConstantFlowValveSleepMonthDialog.show();
    }

    /**
     * 显示调整上传间隔的弹窗
     *
     * @param meterId              设备编号
     * @param socketPackage        Socket包的实体类对象
     * @param constantFlowValveCmd 恒流阀指令实体类的对象
     */
    private void showChangeUploadIntervalDialog(String meterId, SocketPackage socketPackage, ConstantFlowValveCmd constantFlowValveCmd) {
        ChangeConstantFlowValveUploadIntervalDialog changeConstantFlowValveUploadIntervalDialog = new ChangeConstantFlowValveUploadIntervalDialog(mContext);
        changeConstantFlowValveUploadIntervalDialog.setCancelable(true);
        EditText etInterval = changeConstantFlowValveUploadIntervalDialog.findViewById(R.id.et_interval);
        changeConstantFlowValveUploadIntervalDialog.setOnDialogClickListener(new ChangeConstantFlowValveUploadIntervalDialog.OnDialogClickListener() {
            @Override
            public void onSetIntervalClick() {
                //设置休眠月份
                socketPackage.setCmdCode(51);
                String interval = etInterval.getText().toString();
                if (TextUtils.isEmpty(interval)) {
                    showToast("请输入上传间隔");
                    return;
                }
                constantFlowValveCmd.setLiuliang(interval);
                socketPackage.setCmdContent(GsonUtils.convertJSON(constantFlowValveCmd));
                operateMeterBySocket(socketPackage);
                refreshLogList("恒流阀" + meterId + "设置上传间隔指令已发送");
                changeConstantFlowValveUploadIntervalDialog.dismiss();
            }
        });
        changeConstantFlowValveUploadIntervalDialog.show();
    }

    /**
     * 清空读表的表格数据
     */
    private void clearTableData() {
        meterIdTv.setText("");
        totalConsumptionTv.setText("");
        flowRateTv.setText("");
        tvQuanShu.setText("");
        tvSleepTime.setText("");
        tvTemperature.setText("");
        tvUploadInterval.setText("");
        valveStatusTv.setText("");
        meterStatusTv.setText("");
        batteryVoltageTv.setText("");
        meterTimeTv.setText("");
    }

    /**
     * 通过Socket通信操作水表
     *
     * @param socketPackage SocketPackage对象
     */
    public void operateMeterBySocket(SocketPackage socketPackage) {
        Runnable runnable = () -> {
            if (isConnectSuccess) {
                socketService.sendMsg(NetWork.ANDROID_CMD + GsonUtils.convertJSON(socketPackage) + NetWork.ANDROID_END);
            } else {
                showToast("通信服务未建立，发送失败");
                refreshLogList(TimeUtils.getCurrentTimeWithSpace() + "通信服务未建立，发送失败\n");
            }
        };
        executorService.submit(runnable);
    }

    /**
     * 刷新日志列表
     *
     * @param msg 新增文本信息
     */
    private void refreshLogList(String msg) {
        Log log = new Log();
        log.setTime(TimeUtils.getCurrentTime());
        log.setContent(msg);
        logList.add(0, log);
        //最多显示100条
        if (logList.size() > 100) {
            logList.remove(logList.size() - 1);
        }
        logAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        Intent intent = new Intent(mContext, SocketService.class);
        stopService(intent);
        executorService.shutdown();
    }
}