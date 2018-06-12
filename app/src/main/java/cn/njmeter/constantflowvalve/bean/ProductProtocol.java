package cn.njmeter.constantflowvalve.bean;

/**
 * 产品公共属性的综合实体类，根据不同产品选择部分进行赋值和取值
 * Created by LiYuliang on 2017/08/25 0025.
 *
 * @author LiYuliang
 * @version 2017/11/20
 */

public class ProductProtocol {
    /**
     * 表号
     */
    private String MeterID = "";
    /**
     * ModBus主机地址
     */
    private String MBUSAddress = "";
    /**
     * 产品类型
     */
    private String ProductTypeTX = "";
    /**
     * 累计冷量
     */
    private double SumCool = 0;
    /**
     * 累计冷量单位
     */
    private String SumCoolUnit = "";
    /**
     * 累计热量
     */
    private double SumHeat = 0;
    /**
     * 累计热量单位
     */
    private String SumHeatUnit = "";
    /**
     * 正向总流量
     */
    private double Total = 0;
    /**
     * 正向流量单位
     */
    private String TotalUnit = "";
    /**
     * 反向总流量
     */
    private double OppositeTotal = 0;
    /**
     * 反向流量单位
     */
    private String OppositeTotalUnit = "";
    /**
     * 功率（仅热表）
     */
    private double Power = 0;
    /**
     * 功率单位
     */
    private String PowerUnit = "";
    /**
     * 瞬时流速
     */
    private double FlowRate = 0;
    /**
     * 瞬时流速单位
     */
    private String FlowRateUnit = "";
    /**
     * 总开阀时间
     */
    private double SumOpenValveM = 0;
    /**
     * 关闭时间
     */
    private String CloseTime = "";
    /**
     * 断电时间
     */
    private String LosePowerTime = "";
    /**
     * 断开连接时间
     */
    private String LoseConTime = "";
    private double InsideT = 0;
    private String InsideTSet = "";
    /**
     * 阀门状态
     */
    private String ValveStatus = "";
    /**
     * 热表进水温度，水表水温
     */
    private double T1InP = 0;
    /**
     * 热表出水温度
     */
    private double T2InP = 0;
    /**
     * 累计工作时间
     */
    private double WorkTimeInP = 0;
    /**
     * 表具时间
     */
    private String TimeInP = "";
    /**
     * 电池电压
     */
    private String Vol = "";
    /**
     * 状态
     */
    private String Status = "";
    /**
     * 圈数
     */
    private int quanShu = 0;
    /**
     * 休眠起始月份
     */
    private int startSleep = 0;
    /**
     * 休眠结束月份
     */
    private int stopSleep = 0;
    /**
     * 上传间隔（小时）
     */
    private int uploadInterval = 0;

    public String getMeterID() {
        return MeterID;
    }

    public void setMeterID(String meterID) {
        MeterID = meterID;
    }

    public String getMBUSAddress() {
        return MBUSAddress;
    }

    public void setMBUSAddress(String mBUSAddress) {
        MBUSAddress = mBUSAddress;
    }

    public String getProductTypeTX() {
        return ProductTypeTX;
    }

    public void setProductTypeTX(String productTypeTX) {
        ProductTypeTX = productTypeTX;
    }

    public double getSumCool() {
        return SumCool;
    }

    public void setSumCool(double sumCool) {
        SumCool = sumCool;
    }

    public String getSumCoolUnit() {
        return SumCoolUnit;
    }

    public void setSumCoolUnit(String sumCoolUnit) {
        SumCoolUnit = sumCoolUnit;
    }

    public double getSumHeat() {
        return SumHeat;
    }

    public void setSumHeat(double sumHeat) {
        SumHeat = sumHeat;
    }

    public String getSumHeatUnit() {
        return SumHeatUnit;
    }

    public void setSumHeatUnit(String sumHeatUnit) {
        SumHeatUnit = sumHeatUnit;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public String getTotalUnit() {
        return TotalUnit;
    }

    public void setTotalUnit(String totalUnit) {
        TotalUnit = totalUnit;
    }

    public double getOppositeTotal() {
        return OppositeTotal;
    }

    public void setOppositeTotal(double oppositeTotal) {
        OppositeTotal = oppositeTotal;
    }

    public String getOppositeTotalUnit() {
        return OppositeTotalUnit;
    }

    public void setOppositeTotalUnit(String oppositeTotalUnit) {
        OppositeTotalUnit = oppositeTotalUnit;
    }

    public double getPower() {
        return Power;
    }

    public void setPower(double power) {
        Power = power;
    }

    public String getPowerUnit() {
        return PowerUnit;
    }

    public void setPowerUnit(String powerUnit) {
        PowerUnit = powerUnit;
    }

    public double getFlowRate() {
        return FlowRate;
    }

    public void setFlowRate(double flowRate) {
        FlowRate = flowRate;
    }

    public String getFlowRateUnit() {
        return FlowRateUnit;
    }

    public void setFlowRateUnit(String flowRateUnit) {
        FlowRateUnit = flowRateUnit;
    }

    public double getSumOpenValveM() {
        return SumOpenValveM;
    }

    public void setSumOpenValveM(double sumOpenValveM) {
        SumOpenValveM = sumOpenValveM;
    }

    public String getCloseTime() {
        return CloseTime;
    }

    public void setCloseTime(String closeTime) {
        CloseTime = closeTime;
    }

    public String getLosePowerTime() {
        return LosePowerTime;
    }

    public void setLosePowerTime(String losePowerTime) {
        LosePowerTime = losePowerTime;
    }

    public String getLoseConTime() {
        return LoseConTime;
    }

    public void setLoseConTime(String loseConTime) {
        LoseConTime = loseConTime;
    }

    public double getInsideT() {
        return InsideT;
    }

    public void setInsideT(double insideT) {
        InsideT = insideT;
    }

    public String getInsideTSet() {
        return InsideTSet;
    }

    public void setInsideTSet(String insideTSet) {
        InsideTSet = insideTSet;
    }

    public String getValveStatus() {
        return ValveStatus;
    }

    public void setValveStatus(String valveStatus) {
        ValveStatus = valveStatus;
    }

    public double getT1InP() {
        return T1InP;
    }

    public void setT1InP(double t1InP) {
        T1InP = t1InP;
    }

    public double getT2InP() {
        return T2InP;
    }

    public void setT2InP(double t2InP) {
        T2InP = t2InP;
    }

    public double getWorkTimeInP() {
        return WorkTimeInP;
    }

    public void setWorkTimeInP(double workTimeInP) {
        WorkTimeInP = workTimeInP;
    }

    public String getTimeInP() {
        return TimeInP;
    }

    public void setTimeInP(String timeInP) {
        TimeInP = timeInP;
    }

    public String getVol() {
        return Vol;
    }

    public void setVol(String vol) {
        Vol = vol;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getQuanShu() {
        return quanShu;
    }

    public void setQuanShu(int quanShu) {
        this.quanShu = quanShu;
    }

    public int getStartSleep() {
        return startSleep;
    }

    public void setStartSleep(int startSleep) {
        this.startSleep = startSleep;
    }

    public int getStopSleep() {
        return stopSleep;
    }

    public void setStopSleep(int stopSleep) {
        this.stopSleep = stopSleep;
    }

    public int getUploadInterval() {
        return uploadInterval;
    }

    public void setUploadInterval(int uploadInterval) {
        this.uploadInterval = uploadInterval;
    }
}
