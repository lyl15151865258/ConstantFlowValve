package cn.njmeter.constantflowvalve.bean;

import java.util.List;

/**
 * Created by LiYuliang on 2017/8/31.
 * 水司层级关系请求结果实体类
 *
 * @author LiYuliang
 * @version 2017/11/29
 */

public class WaterCompanyHierarchy {

    private String result;

    private List<Data> data;

    private String msg;

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return this.result;
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
        private int entranceId;

        private int exchangStationId;

        private int villageId;

        private String building;

        private String village;

        private int supplierId;

        private String exchangStation;

        private String supplier;

        private String entrance;

        private int buildingId;

        public void setEntranceId(int entranceId) {
            this.entranceId = entranceId;
        }

        public int getEntranceId() {
            return this.entranceId;
        }

        public void setExchangStationId(int exchangeStationId) {
            this.exchangStationId = exchangeStationId;
        }

        public int getExchangStationId() {
            return this.exchangStationId;
        }

        public void setVillageId(int villageId) {
            this.villageId = villageId;
        }

        public int getVillageId() {
            return this.villageId;
        }

        public void setBuilding(String building) {
            this.building = building;
        }

        public String getBuilding() {
            return this.building;
        }

        public void setVillage(String village) {
            this.village = village;
        }

        public String getVillage() {
            return this.village;
        }

        public void setSupplierId(int supplierId) {
            this.supplierId = supplierId;
        }

        public int getSupplierId() {
            return this.supplierId;
        }

        public void setExchangStation(String exchangStation) {
            this.exchangStation = exchangStation;
        }

        public String getExchangStation() {
            return this.exchangStation;
        }

        public void setSupplier(String supplier) {
            this.supplier = supplier;
        }

        public String getSupplier() {
            return this.supplier;
        }

        public void setEntrance(String entrance) {
            this.entrance = entrance;
        }

        public String getEntrance() {
            return this.entrance;
        }

        public void setBuildingId(int buildingId) {
            this.buildingId = buildingId;
        }

        public int getBuildingId() {
            return this.buildingId;
        }

    }
}
