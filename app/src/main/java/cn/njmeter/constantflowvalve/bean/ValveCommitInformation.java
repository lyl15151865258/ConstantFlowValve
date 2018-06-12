package cn.njmeter.constantflowvalve.bean;

import java.util.List;

/**
 * Created by LiYuliang on 2017/4/15 0015.
 * 水表上传信息的实体类
 */

public class ValveCommitInformation {

    private String result;

    private int totalCount;

    private List<Data> data;

    private String msg;

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return this.result;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
        return this.data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public class Data {

        private double total;

        private double dt;

        private double initCool;

        private String createTime;

        private int endDormancyTime;

        private double t1Inp;

        private double sumCool;

        private int errInt;

        private int flowPoint;

        private String valveStatus;

        private double creditCool;

        private double totalCreditHeat;

        private String entrance;

        private String remark2;

        private double creditHeat;

        private String village;

        private int userId;

        private String userName;

        private double workTimeInp;

        private String doorPlate;

        private String sampleTime;

        private double flowRate;

        private double powerW;

        private double totalCreditCool;

        private int meterIdId;

        private double creditTotal;

        private double totalCreditTotal;

        private String building;

        private double insideT;

        private double pressure;

        private String status;

        private int rotateNum;

        private double electric;

        private double sumOpenValveM;

        private int uploadInterval;

        private double sumHeat;

        private double initTotal;

        private double initHeat;

        private String meterId;

        private double sumOpenValveH;

        private double sumHeatGJ;

        private int startDormancyTime;

        private String meterSize;

        private double t2Inp;

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public double getDt() {
            return dt;
        }

        public void setDt(double dt) {
            this.dt = dt;
        }

        public double getInitCool() {
            return initCool;
        }

        public void setInitCool(double initCool) {
            this.initCool = initCool;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getEndDormancyTime() {
            return endDormancyTime;
        }

        public void setEndDormancyTime(int endDormancyTime) {
            this.endDormancyTime = endDormancyTime;
        }

        public double getT1Inp() {
            return t1Inp;
        }

        public void setT1Inp(double t1Inp) {
            this.t1Inp = t1Inp;
        }

        public double getSumCool() {
            return sumCool;
        }

        public void setSumCool(double sumCool) {
            this.sumCool = sumCool;
        }

        public int getErrInt() {
            return errInt;
        }

        public void setErrInt(int errInt) {
            this.errInt = errInt;
        }

        public int getFlowPoint() {
            return flowPoint;
        }

        public void setFlowPoint(int flowPoint) {
            this.flowPoint = flowPoint;
        }

        public String getValveStatus() {
            return valveStatus;
        }

        public void setValveStatus(String valveStatus) {
            this.valveStatus = valveStatus;
        }

        public double getCreditCool() {
            return creditCool;
        }

        public void setCreditCool(double creditCool) {
            this.creditCool = creditCool;
        }

        public double getTotalCreditHeat() {
            return totalCreditHeat;
        }

        public void setTotalCreditHeat(double totalCreditHeat) {
            this.totalCreditHeat = totalCreditHeat;
        }

        public String getEntrance() {
            return entrance;
        }

        public void setEntrance(String entrance) {
            this.entrance = entrance;
        }

        public String getRemark2() {
            return remark2;
        }

        public void setRemark2(String remark2) {
            this.remark2 = remark2;
        }

        public double getCreditHeat() {
            return creditHeat;
        }

        public void setCreditHeat(double creditHeat) {
            this.creditHeat = creditHeat;
        }

        public String getVillage() {
            return village;
        }

        public void setVillage(String village) {
            this.village = village;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public double getWorkTimeInp() {
            return workTimeInp;
        }

        public void setWorkTimeInp(double workTimeInp) {
            this.workTimeInp = workTimeInp;
        }

        public String getDoorPlate() {
            return doorPlate;
        }

        public void setDoorPlate(String doorPlate) {
            this.doorPlate = doorPlate;
        }

        public String getSampleTime() {
            return sampleTime;
        }

        public void setSampleTime(String sampleTime) {
            this.sampleTime = sampleTime;
        }

        public double getFlowRate() {
            return flowRate;
        }

        public void setFlowRate(double flowRate) {
            this.flowRate = flowRate;
        }

        public double getPowerW() {
            return powerW;
        }

        public void setPowerW(double powerW) {
            this.powerW = powerW;
        }

        public double getTotalCreditCool() {
            return totalCreditCool;
        }

        public void setTotalCreditCool(double totalCreditCool) {
            this.totalCreditCool = totalCreditCool;
        }

        public int getMeterIdId() {
            return meterIdId;
        }

        public void setMeterIdId(int meterIdId) {
            this.meterIdId = meterIdId;
        }

        public double getCreditTotal() {
            return creditTotal;
        }

        public void setCreditTotal(double creditTotal) {
            this.creditTotal = creditTotal;
        }

        public double getTotalCreditTotal() {
            return totalCreditTotal;
        }

        public void setTotalCreditTotal(double totalCreditTotal) {
            this.totalCreditTotal = totalCreditTotal;
        }

        public String getBuilding() {
            return building;
        }

        public void setBuilding(String building) {
            this.building = building;
        }

        public double getInsideT() {
            return insideT;
        }

        public void setInsideT(double insideT) {
            this.insideT = insideT;
        }

        public double getPressure() {
            return pressure;
        }

        public void setPressure(double pressure) {
            this.pressure = pressure;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getRotateNum() {
            return rotateNum;
        }

        public void setRotateNum(int rotateNum) {
            this.rotateNum = rotateNum;
        }

        public double getElectric() {
            return electric;
        }

        public void setElectric(double electric) {
            this.electric = electric;
        }

        public double getSumOpenValveM() {
            return sumOpenValveM;
        }

        public void setSumOpenValveM(double sumOpenValveM) {
            this.sumOpenValveM = sumOpenValveM;
        }

        public int getUploadInterval() {
            return uploadInterval;
        }

        public void setUploadInterval(int uploadInterval) {
            this.uploadInterval = uploadInterval;
        }

        public double getSumHeat() {
            return sumHeat;
        }

        public void setSumHeat(double sumHeat) {
            this.sumHeat = sumHeat;
        }

        public double getInitTotal() {
            return initTotal;
        }

        public void setInitTotal(double initTotal) {
            this.initTotal = initTotal;
        }

        public double getInitHeat() {
            return initHeat;
        }

        public void setInitHeat(double initHeat) {
            this.initHeat = initHeat;
        }

        public String getMeterId() {
            return meterId;
        }

        public void setMeterId(String meterId) {
            this.meterId = meterId;
        }

        public double getSumOpenValveH() {
            return sumOpenValveH;
        }

        public void setSumOpenValveH(double sumOpenValveH) {
            this.sumOpenValveH = sumOpenValveH;
        }

        public double getSumHeatGJ() {
            return sumHeatGJ;
        }

        public void setSumHeatGJ(double sumHeatGJ) {
            this.sumHeatGJ = sumHeatGJ;
        }

        public int getStartDormancyTime() {
            return startDormancyTime;
        }

        public void setStartDormancyTime(int startDormancyTime) {
            this.startDormancyTime = startDormancyTime;
        }

        public String getMeterSize() {
            return meterSize;
        }

        public void setMeterSize(String meterSize) {
            this.meterSize = meterSize;
        }

        public double getT2Inp() {
            return t2Inp;
        }

        public void setT2Inp(double t2Inp) {
            this.t2Inp = t2Inp;
        }
    }
}
