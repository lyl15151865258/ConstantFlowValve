package cn.njmeter.constantflowvalve.sqlite.table;

public class Table {

    /**
     * 保存消火栓超级管理员选择公司记录的表
     *
     * @author LiYuliang
     * @date 2018/4/9 15:25
     */
    public class HydrantCompanySearchTable {
        public static final String TABLE_NAME = "hydrantCompany_record";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String HTTP_PORT = "httpPort";
    }

    /**
     * 保存供热平台超级管理员选择公司记录的表
     * Created at 2018/5/23 0023 20:49
     *
     * @author LiYuliang
     * @version 1.0
     */
    public class HeatCompanySearchTable {
        public static final String TABLE_NAME = "HeatCompanyRecord";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String HTTP_PORT = "httpPort";
    }

    /**
     * 保存水表平台管理员选择水司层级记录的表
     *
     * @author LiYuliang
     * @date 2018/4/9 15:20
     */
    public class HierarchySearchTable {
        public static final String TABLE_NAME = "hierarchy_record";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String FIELD_NAME = "fieldName";
        public static final String FIELD_VALUE = "fieldValue";
    }

    /**
     * 保存水表管理员选择水司的表（按照平台区分）
     *
     * @author LiYuliang
     * @date 2018/4/9 15:11
     */
    public class WaterCompanySearchTable {
        public static final String TABLE_NAME = "waterCompany_record";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String HTTP_PORT = "httpPort";
    }

    /**
     * 保存水司管理员收藏的采集器号和表号（水表操作页面）
     *
     * @author LiYuliang
     * @date 2018/4/9 14:00
     */
    public class WaterMeterSaveTable {
        public static final String TABLE_NAME = "waterMeter_save";

        public static final String ID = "id";
        public static final String IMEI = "imei";
        public static final String METER_ID = "meterId";
    }

    /**
     * 保存机械水表抄表记录的表
     *
     * @author LiYuliang
     * @date 2018/4/9 14:21
     */
    public class WaterMeterRecordTable {
        public static final String TABLE_NAME = "ReadWaterMeterRecord";

        public static final String ID = "id";
        public static final String AREA_CODE = "areaCode";
        public static final String AREA_NAME = "areaName";
        public static final String USER_CODE = "userCode";
        public static final String USER_NAME = "userName";
        public static final String USER_PHONE = "userPhone";
        public static final String USER_ADDRESS = "userAddress";
        public static final String METER_CODE = "meterCode";
        public static final String METER_TYPE = "meterType";
        public static final String METER_LAST_DATA = "meterLastData";
        public static final String LAST_CONSUMPTION = "lastConsumption";
        public static final String USER_NO = "userNo";
        public static final String COMMIT_STATUS = "commitStatus";

        public static final String METER_THIS_DATA = "meterThisData";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String ALTITUDE = "altitude";
        public static final String PHOTO_PATHS = "photoPaths";
        public static final String METER_READER = "meterReader";
        public static final String READ_TIME = "readTime";
        public static final String METER_STATUS = "meterStatus";
        public static final String READ_STATUS = "readStatus";
    }

    /**
     * 保存用户离线操作蓝牙的记录
     *
     * @author LiYuliang
     * @date 2018/4/9 15:28
     */
    public class BluetoothRecordTable {
        public static final String TABLE_NAME = "bluetooth_record";

        public static final String ID = "id";
        public static final String LOGIN_ID = "loginId";
        public static final String LAT = "lat";
        public static final String LNG = "lng";
        public static final String TX = "tx";
        public static final String MODE = "mode";
        public static final String TIME = "time";
        public static final String CREATE_TIME = "createTime";
        public static final String BLUETOOTH_MAC = "bluetoothMac";
        public static final String PRODUCT_TYPE = "productType";
        public static final String BLUETOOTH_DESC = "bluetoothDesc";
    }

    /**
     * 产品与服务搜索记录
     * Created at 2018/4/25 0025 13:45
     *
     * @author LiYuliang
     * @version 1.0
     */
    public class ProductSearchTable {
        public static final String TABLE_NAME = "searchProduct_record";

        public static final String ID = "id";
        public static final String NAME = "name";
    }
}
