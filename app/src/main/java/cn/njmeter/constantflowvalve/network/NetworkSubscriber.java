package cn.njmeter.constantflowvalve.network;

import android.content.Context;
import android.util.Log;

import rx.Subscriber;

/**
 * Subscriber基类,可以在这里处理client网络连接状况（比如没有wifi，没有4g，没有联网等）
 * Created by Li Yuliang on 2018/04/02 0002.
 *
 * @author LiYuliang
 * @version 2018/04/02
 */

public abstract class NetworkSubscriber<T> extends Subscriber<T> {

    private Context context;
    private String className;

    public NetworkSubscriber(Context context, String className) {
        this.context = context;
        this.className = className;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("tag", "NetworkSubscriber.onStart()");
        //接下来可以检查网络连接等操作
//        if (!NetworkUtil.isNetworkAvailable(context)) {
//            EventBus.getDefault().post(new MessageEvent("当前网络不可用，请检查网络", className));
//            // 取消本次Subscriber订阅
//            if (!isUnsubscribed()) {
//                unsubscribe();
//            }
//        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e("tag", "NetworkSubscriber.throwable =" + e.toString());
        Log.e("tag", "NetworkSubscriber.throwable =" + e.getMessage());

        if (e instanceof Exception) {
            //访问获得对应的Exception
            onError(ExceptionHandle.handleException(e));
        } else {
            //将Throwable 和 未知错误的status code返回
            onError(new ExceptionHandle.ResponseThrowable(e, ExceptionHandle.ERROR.UNKNOWN));
        }
    }

    public abstract void onError(ExceptionHandle.ResponseThrowable responseThrowable);

    @Override
    public void onCompleted() {
        Log.i("tag", "NetworkSubscriber.onComplete()");
    }

}
