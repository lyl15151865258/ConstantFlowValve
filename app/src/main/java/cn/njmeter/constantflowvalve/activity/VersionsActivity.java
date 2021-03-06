package cn.njmeter.constantflowvalve.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.njmeter.constantflowvalve.BuildConfig;
import cn.njmeter.constantflowvalve.ConstantFlowValveApplication;
import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.adapter.VersionLogAdapter;
import cn.njmeter.constantflowvalve.bean.Version;
import cn.njmeter.constantflowvalve.bean.VersionLog;
import cn.njmeter.constantflowvalve.bean.ClientUser;
import cn.njmeter.constantflowvalve.constant.ApkInfo;
import cn.njmeter.constantflowvalve.constant.Constants;
import cn.njmeter.constantflowvalve.network.ExceptionHandle;
import cn.njmeter.constantflowvalve.network.NetClient;
import cn.njmeter.constantflowvalve.network.NetworkSubscriber;
import cn.njmeter.constantflowvalve.utils.ActivityController;
import cn.njmeter.constantflowvalve.utils.ApkUtils;
import cn.njmeter.constantflowvalve.utils.FileUtil;
import cn.njmeter.constantflowvalve.utils.LogUtils;
import cn.njmeter.constantflowvalve.utils.MathUtils;
import cn.njmeter.constantflowvalve.utils.NetworkUtil;
import cn.njmeter.constantflowvalve.widget.MyProgressBar;
import cn.njmeter.constantflowvalve.widget.MyToolbar;
import cn.njmeter.constantflowvalve.widget.RecyclerViewDivider;
import cn.njmeter.constantflowvalve.widget.DownLoadDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * App历史版本页面
 * Created by Li Yuliang on 2017/2/20.
 *
 * @author LiYuliang
 * @version 2017/10/27
 */

public class VersionsActivity extends BaseActivity {

    private Context mContext;

    private RecyclerView lvVersionLog;
    private String versionFileName, latestVersionMD5, apkDownloadPath;
    private MyProgressBar myProgressBar;
    private TextView tvCompletedSize, tvTotalSize;
    private float apkSize, completedSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, getString(R.string.VersionView), R.drawable.back_white, onClickListener);
        ((TextView) findViewById(R.id.tv_version)).setText(ApkUtils.getVersionName(this));
        ClientUser.Account account = ConstantFlowValveApplication.getInstance().getAccount();
        TextView tv_updateMode = findViewById(R.id.tv_updateMode);
        if (account == null) {
            tv_updateMode.setText("");
        } else {
            if (account.getStable_Update() == 1 && account.getBeta_Update() == 1) {
                tv_updateMode.setText("已开启预览版更新");
            } else if (account.getStable_Update() == 1 && account.getBeta_Update() == 0) {
                tv_updateMode.setText("已开启正式版更新");
            } else if (account.getStable_Update() == 0) {
                tv_updateMode.setText("已禁用所有更新");
            }
        }
        lvVersionLog = findViewById(R.id.lv_versionLog);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lvVersionLog.setLayoutManager(linearLayoutManager);
        lvVersionLog.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL, 2, ContextCompat.getColor(this, R.color.gray_slight)));

        Map<String, String> params = new HashMap<>(1);
        params.put("apkTypeId", ApkInfo.APK_TYPE_ID_CONSTANT_FLOW_VALVE);
        Observable<VersionLog> versionLogCall = NetClient.getInstances(NetClient.BASE_URL_PROJECT).getNjMeterApi().getVersionLog(params);
        versionLogCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<VersionLog>(mContext, getClass().getSimpleName()) {

            @Override
            public void onStart() {
                super.onStart();
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast("当前网络不可用，请检查网络");
//                    if (!isUnsubscribed()) {
//                        unsubscribe();
//                    }
                } else {
                    showLoadingDialog(mContext, "查询中", true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast(responseThrowable.message);
            }

            @Override
            public void onNext(VersionLog versionLog) {
                cancelDialog();
                if (versionLog == null || versionLog.getVersion().size() < 1) {
                    showToast("版本更新日志获取失败");
                } else {
                    List<Version> versionList = versionLog.getVersion();
                    VersionLogAdapter versionLogAdapter = new VersionLogAdapter(VersionsActivity.this, versionList);
                    lvVersionLog.setAdapter(versionLogAdapter);
                    versionLogAdapter.buttonSetOnclick((view, position) -> {
                        versionFileName = versionList.get(position).getVersionFileName();
                        latestVersionMD5 = versionList.get(position).getMd5Value();
                        apkDownloadPath = versionList.get(position).getVersionUrl().replace("\\", "/");
                        LogUtils.d("DownloadPath", apkDownloadPath);
                        if (isDownloaded()) {
                            //如果已经下载了，则弹出窗口询问直接安装还是重新下载
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            // 设置提示框的标题
                            builder.setTitle("友情提示").
                                    setIcon(R.drawable.ic_launcher).
                                    setMessage("您已经下载过该版本文件，是否直接安装").
                                    setPositiveButton("直接安装", (dialog, which) -> {
                                        //直接安装
                                        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), versionFileName);
                                        installApk(file);
                                    }).
                                    setNegativeButton("重新下载", (dialog, which) -> downloadApk());
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else {
                            downloadApk();
                        }
                    });
                }
            }
        });
    }

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(this);
                break;
            default:
                break;
        }
    };

    /**
     * 判断是否已经下载过该文件
     *
     * @return boolean
     */
    private boolean isDownloaded() {
        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + versionFileName);
        LogUtils.d("MD5", file.getPath());
        return file.isFile() && latestVersionMD5.equals(FileUtil.getFileMD5(file));
    }

    /**
     * 下载新版本程序
     */
    private void downloadApk() {
        if (apkDownloadPath.equals(Constants.EMPTY)) {
            showToast("下载路径有误，请联系客服");
        } else {
            DownLoadDialog progressDialog = new DownLoadDialog(mContext);
            myProgressBar = progressDialog.findViewById(R.id.progressbar_download);
            tvCompletedSize = progressDialog.findViewById(R.id.tv_completedSize);
            tvTotalSize = progressDialog.findViewById(R.id.tv_totalSize);
            progressDialog.setCancelable(false);
            progressDialog.show();
            NetClient.downloadFileProgress(apkDownloadPath, (currentBytes, contentLength, done) -> {
                //获取到文件的大小
                apkSize = MathUtils.formatFloat((float) contentLength / 1024f / 1024f, 2);
                tvTotalSize.setText(String.format(mContext.getString(R.string.file_size_m), String.valueOf(apkSize)));
                //已完成大小
                completedSize = MathUtils.formatFloat((float) currentBytes / 1024f / 1024f, 2);
                tvCompletedSize.setText(String.format(mContext.getString(R.string.file_size_m), String.valueOf(completedSize)));
                myProgressBar.setProgress(MathUtils.formatFloat(completedSize / apkSize * 100, 1));
                if (done) {
                    progressDialog.dismiss();
                }
            }, new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    //处理下载文件
                    if (response.body() != null) {
                        try {
                            InputStream is = response.body().byteStream();
                            //定义下载后文件的路径和名字，例如：/Download/JiangSuMetter_1.0.1.apk
                            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + versionFileName);
                            FileOutputStream fos = new FileOutputStream(file);
                            BufferedInputStream bis = new BufferedInputStream(is);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = bis.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                            bis.close();
                            is.close();
                            installApk(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast("下载出错，" + e.getMessage() + "，请联系管理员");
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    progressDialog.dismiss();
                    showToast("下载出错，" + t.getMessage() + "，请联系管理员");
                }
            });
        }
    }

    /**
     * 安装apk
     *
     * @param file 需要安装的apk
     */
    private void installApk(File file) {
        //先验证文件的正确性和完整性（通过MD5值）
        if (file.isFile() && latestVersionMD5.equals(FileUtil.getFileMD5(file))) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);//在AndroidManifest中的android:authorities值
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            startActivity(intent);
        } else {
            showToast("文件异常，无法安装");
        }
    }
}
