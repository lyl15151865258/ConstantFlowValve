package cn.njmeter.constantflowvalve.bean;

/**
 * Socket通信操作恒流阀具体内容实体类
 * Created at 2018/5/24 0024 14:45
 *
 * @author LiYuliang
 * @version 1.0
 */

public class ConstantFlowValveCmd {

    private String liuliang = "0";
    private String quanshu = "0";

    public String getLiuliang() {
        return liuliang;
    }

    public void setLiuliang(String liuliang) {
        this.liuliang = liuliang;
    }

    public String getQuanshu() {
        return quanshu;
    }

    public void setQuanshu(String quanshu) {
        this.quanshu = quanshu;
    }
}
