package cn.njmeter.constantflowvalve.network;

import java.util.List;
import java.util.Map;

import cn.njmeter.constantflowvalve.bean.HeatMeterLastDataResult;
import cn.njmeter.constantflowvalve.bean.VersionLog;
import cn.njmeter.constantflowvalve.bean.NormalResult;
import cn.njmeter.constantflowvalve.bean.ClientUser;
import cn.njmeter.constantflowvalve.bean.HeatCompanyHierarchy;
import cn.njmeter.constantflowvalve.bean.ValveCommitInformation;
import cn.njmeter.constantflowvalve.bean.ValveLoginResult;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Retrofit网络请求构建接口
 * Created by LiYuliang on 2017/10/28.
 *
 * @author LiYuliang
 * @version 2017/10/19
 */

public interface NjMeterApi {

    /********************************************************************  主账号相关接口  *********************************************************************

     /**
     * 主账号注册的请求
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("AndroidController/register.do")
    Observable<ClientUser> registerMainAccount(@FieldMap Map<String, String> params);

    /**
     * 主账号登录的请求
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("AndroidController/newLoginAndroid.do")
    Observable<String> loginMainAccount(@FieldMap Map<String, String> params);

    /**
     * 软件历史版本更新日志信息
     *
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("AndroidController/getNewVersionUpdateLog.do")
    Observable<VersionLog> getVersionLog(@FieldMap Map<String, String> params);

    /**
     * 子账号更新信息的请求
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("AndroidController/updateLogin.do")
    Observable<ClientUser> updateSubAccount(@FieldMap Map<String, String> params);

    /**
     * 更新主账号昵称
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("AndroidController/updateNewLoginInfo.do")
    Observable<NormalResult> modifyNickName(@FieldMap Map<String, String> params);

    /**
     * 更新主账号密码
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("AndroidController/updateNewPassword.do")
    Observable<NormalResult> updatePassword(@FieldMap Map<String, String> params);

    /**
     * 重置（找回）主账号密码
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("AndroidController/updatePassword.do")
    Observable<NormalResult> resetPassword(@FieldMap Map<String, String> params);

    /**
     * 更新单位信息
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("AndroidController/modifyUserInfo.do")
    Observable<NormalResult> updateCompany(@FieldMap Map<String, String> params);

    /**
     * 提交实名认证信息
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("AndroidController/modifyCertification.do")
    Observable<NormalResult> commitRealName(@FieldMap Map<String, String> params);

    /**
     * 更新用户头像
     *
     * @param information 描述信息
     * @param file        头像文件
     * @return 更新结果
     */
    @Multipart
    @POST("AndroidController/uploadHeadPortrait.do")
    Observable<NormalResult> uploadUserIcon(@Part("information") RequestBody information, @Part MultipartBody.Part file);

    /**
     * 上传错误日志文件
     *
     * @param file 错误日志文件
     * @return 上传结果
     */
    @Multipart
    @POST("AndroidController/uploadAndroidLog.do")
    Observable<NormalResult> uploadCrashFiles(@Part MultipartBody.Part file);

    /**
     * 更改是否接收测试版本
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("AndroidController/updateBetaUpdate.do")
    Observable<NormalResult> changeBetaVersionUpdate(@FieldMap Map<String, Integer> params);

    /**
     * 提交软件反馈信息
     *
     * @param information 描述信息
     * @param parts       图片文件
     * @return 上传结果
     */
    @Multipart
    @POST("AndroidController/uploadFeedback.do")
    Observable<NormalResult> uploadFeedback(@Part("information") RequestBody information, @Part List<MultipartBody.Part> parts);

    /**
     * 下载软件
     *
     * @param params 文件类型参数
     * @return ResponseBody
     */
    @FormUrlEncoded
    @POST("VersionController/downloadNewVersion.do")
    Call<ResponseBody> downloadFile(@FieldMap Map<String, String> params);

    /**
     * 下载软件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    @GET
    Call<ResponseBody> downloadFile(@Url String filePath);


    /******************************************************************  智能恒流阀相关接口  ******************************************************************

     /**
     * 消火栓平台登录
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("android/androidLogin.do")
    Observable<ValveLoginResult> loginWaterMeter(@FieldMap Map<String, String> params);

    /**
     * 查询层级关系
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("android/findLvInfo.do")
    Observable<HeatCompanyHierarchy> searchAllHierarchy(@FieldMap Map<String, String> params);

    /**
     * 查询已抄到数据的表具信息
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("android/findNewLastReportData.do")
    Observable<HeatMeterLastDataResult> searchHeatMeterLastReportYiChao(@FieldMap Map<String, String> params);

    /**
     * 查询未抄到数据的表具信息
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("android/findNewLastReportDataWeichaodao.do")
    Observable<HeatMeterLastDataResult> searchHeatMeterLastReportWeiChao(@FieldMap Map<String, String> params);

    /**
     * 查询一只恒流阀指定日期区间的上传记录
     *
     * @param params 参数
     * @return 返回值
     */
    @FormUrlEncoded
    @POST("android/findNewOneMeterReportData.do")
    Observable<ValveCommitInformation> searchMeterCommitDataInformation(@FieldMap Map<String, String> params);

}
