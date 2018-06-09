package cn.njmeter.constantflowvalve.sqlite.table;

public class Table {

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
}
