package cn.njmeter.constantflowvalve.bean;

import java.util.List;

public class HeatMeterLastDataResult {

    private String result;
    private int count;
    private List<HeatMeterLastData> data;
    private String msg;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<HeatMeterLastData> getData() {
        return data;
    }

    public void setData(List<HeatMeterLastData> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
