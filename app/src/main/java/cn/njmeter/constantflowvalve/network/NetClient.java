package cn.njmeter.constantflowvalve.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.njmeter.constantflowvalve.BuildConfig;
import cn.njmeter.constantflowvalve.ConstantFlowValveApplication;
import cn.njmeter.constantflowvalve.constant.ApkInfo;
import cn.njmeter.constantflowvalve.constant.NetWork;
import cn.njmeter.constantflowvalve.network.retrofit.ProgressListener;
import cn.njmeter.constantflowvalve.network.retrofit.ProgressResponseBody;
import cn.njmeter.constantflowvalve.utils.LogUtils;
import cn.njmeter.constantflowvalve.utils.NetworkUtil;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 请求接口URL
 * Created by Li Yuliang on 2017/2/17 0017.
 *
 * @author LiYuliang
 * @version 2017/10/27
 */

public class NetClient {

    public static final String TAG_POST = "Post方式";
    /**
     * 加密秘钥
     */
    public static final String SECRET_KRY = "jsmt";

    private static NetClient mNetClient;
    private NjMeterApi njMeterApi;
    private final Retrofit mRetrofit;
    private static String defaultUrl = "";
    private static final String CACHE_NAME = "netcache";

    private NetClient(String baseUrl) {

        // log用拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        // 开发模式记录整个body，否则只记录基本信息如返回200，http协议版本等
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }

        //设置缓存目录
        File cacheFile = new File(ConstantFlowValveApplication.getInstance().getExternalCacheDir(), CACHE_NAME);
        //生成缓存，50M
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
        //缓存拦截器
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                //网络不可用
                if (!NetworkUtil.isNetworkAvailable(ConstantFlowValveApplication.getInstance())) {
                    //在请求头中加入：强制使用缓存，不访问网络
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response = chain.proceed(request);
                //网络可用
                if (NetworkUtil.isNetworkAvailable(ConstantFlowValveApplication.getInstance())) {
                    int maxAge = 0;
                    // 有网络时 在响应头中加入：设置缓存超时时间0个小时
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("pragma")
                            .build();
                } else {
                    // 无网络时，在响应头中加入：设置超时为4周
                    int maxStale = 60 * 60 * 24 * 28;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("pragma")
                            .build();
                }
                return response;
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(cacheInterceptor)
                .cache(cache)
                //设置超时时间
                .connectTimeout(NetWork.TIME_OUT_HTTP, TimeUnit.SECONDS)
                .readTimeout(NetWork.TIME_OUT_HTTP, TimeUnit.SECONDS)
                .writeTimeout(NetWork.TIME_OUT_HTTP, TimeUnit.SECONDS)
                //错误重连
                .retryOnConnectionFailure(true)
                .build();
        //设置Gson的非严格模式
        Gson gson = new GsonBuilder().setLenient().create();
        // 初始化Retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    /**
     * 获取单例的NetClient对象
     *
     * @param baseUrl 基础Url
     * @return NetClient对象
     */
    public static synchronized NetClient getInstances(String baseUrl) {
        if (mNetClient == null || !defaultUrl.equals(baseUrl)) {
            mNetClient = new NetClient(baseUrl);
            defaultUrl = baseUrl;
        }
        LogUtils.d("retrofit", baseUrl);
        return mNetClient;
    }

    public NjMeterApi getNjMeterApi() {
        if (njMeterApi == null) {
            njMeterApi = mRetrofit.create(NjMeterApi.class);
        }
        return njMeterApi;
    }

    /**
     * 主账号基础Url不带项目名（用于图像链接中）
     */
    public static final String BASE_URL = "http://" + NetWork.SERVER_HOST_MAIN + ":" + NetWork.SERVER_PORT_MAIN + "/";

    /**
     * 主账号基础Url带项目名
     */
    public static final String BASE_URL_PROJECT = "http://" + NetWork.SERVER_HOST_MAIN + ":" + NetWork.SERVER_PORT_MAIN + "/" + NetWork.PROJECT_MAIN + "/";

    /**
     * 拼接通用基础Url
     *
     * @param serverHost  IP地址（域名）
     * @param httpPort    端口号
     * @param serviceName 项目名
     * @return 拼接后的Url
     */
    public static String getBaseUrl(String serverHost, String httpPort, String serviceName) {
        return "http://" + serverHost + ":" + httpPort + "/" + serviceName + "/";
    }

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(NetWork.TIME_OUT_HTTP, TimeUnit.MILLISECONDS)
            .readTimeout(NetWork.TIME_OUT_HTTP, TimeUnit.MILLISECONDS)
            .writeTimeout(NetWork.TIME_OUT_HTTP, TimeUnit.MILLISECONDS).build();

    /**
     * Retrofit带进度的下载方法（始终下载最新版本）
     *
     * @param listener 进度监听器
     * @param callback 请求结果回调
     */
    public static void downloadFileProgress(ProgressListener listener, Callback<ResponseBody> callback) {
        OkHttpClient client = okHttpClient.newBuilder().addNetworkInterceptor((chain) -> {
            okhttp3.Response response = chain.proceed(chain.request());
            return response.newBuilder().body(new ProgressResponseBody(response.body(), listener)).build();
        }).build();
        //设置Gson的非严格模式
        Gson gson = new GsonBuilder().setLenient().create();
        // 初始化Retrofit
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_PROJECT)
                .client(client)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Map<String, String> params = new HashMap<>(1);
        params.put("apkTypeId", ApkInfo.APK_TYPE_ID_CONSTANT_FLOW_VALVE);
        Call<ResponseBody> downloadCall = mRetrofit.create(NjMeterApi.class).downloadFile(params);
        downloadCall.enqueue(callback);
    }

    /**
     * Retrofit带进度的下载方法（下载指定版本）
     *
     * @param filePath 文件路径
     * @param listener 进度监听器
     * @param callback 请求结果回调
     */
    public static void downloadFileProgress(String filePath, ProgressListener listener, Callback<ResponseBody> callback) {
        OkHttpClient client = okHttpClient.newBuilder().addNetworkInterceptor((chain) -> {
            okhttp3.Response response = chain.proceed(chain.request());
            return response.newBuilder().body(new ProgressResponseBody(response.body(), listener)).build();
        }).build();
        //设置Gson的非严格模式
        Gson gson = new GsonBuilder().setLenient().create();
        // 初始化Retrofit
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Call<ResponseBody> downloadCall = mRetrofit.create(NjMeterApi.class).downloadFile(filePath);
        downloadCall.enqueue(callback);
    }

}
