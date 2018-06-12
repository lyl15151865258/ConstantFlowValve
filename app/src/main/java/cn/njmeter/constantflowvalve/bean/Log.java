package cn.njmeter.constantflowvalve.bean;

/**
 * 日志实体类
 * Created by LiYuliang on 2018/2/7.
 *
 * @author LiYuliang
 * @version 2017/2/7
 */

public class Log {
    /**
     * 时间
     */
    private String time;
    /**
     * 日志内容
     */
    private String content;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
