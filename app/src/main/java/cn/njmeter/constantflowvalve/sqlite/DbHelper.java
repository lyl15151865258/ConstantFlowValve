package cn.njmeter.constantflowvalve.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.njmeter.constantflowvalve.sqlite.table.Table;
import cn.njmeter.constantflowvalve.utils.LogUtils;

/**
 * Created by LiYuliang on 2017/6/5.
 * 数据库操作类
 *
 * @author LiYuliang
 * @version 2017/12/22
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "njmeter.db";
    private static final int DB_VERSION = VersionFactory.getCurrentDBVersion();
    private Context mContext;
    private volatile DbHelper mDbHelper;
    private SQLiteDatabase db;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    public DbHelper getDBHelper() {
        if (mDbHelper == null) {
            synchronized (DbHelper.class) {
                if (mDbHelper == null)
                    mDbHelper = new DbHelper(mContext);
            }
        }
        return mDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableForHeatCompanySearch(db);
        createTableForHierarchySearch(db);
    }

    /**
     * 数据库版本降级
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table.HeatCompanySearchTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Table.HierarchySearchTable.TABLE_NAME);
        onCreate(db);
    }

    /**
     * 数据库版本升级
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDB(db, oldVersion, newVersion);
    }

    /**
     * 数据库版本递归更新
     *
     * @param oldVersion 数据库当前版本号
     * @param newVersion 数据库升级后的版本号
     * @author lh
     */
    private static void updateDB(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.d("dbversion" + "旧版本号：" + oldVersion + "，新版本号：" + newVersion);
        Upgrade upgrade;
        if (oldVersion < newVersion) {
            oldVersion++;
            upgrade = VersionFactory.getUpgrade(oldVersion);
            if (upgrade == null) {
                return;
            }
            upgrade.update(db);
            updateDB(db, oldVersion, newVersion);
        }
    }

    /**
     * 建表——保存供热平台管理员选择公司的表（按照平台区分）
     * Created at 2018/5/23 0023 20:52
     *
     * @author LiYuliang
     * @version 1.0
     */
    private void createTableForHeatCompanySearch(SQLiteDatabase db) {
        String table = "CREATE TABLE IF NOT EXISTS " +
                Table.HeatCompanySearchTable.TABLE_NAME +
                " (" +
                Table.HeatCompanySearchTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Table.HeatCompanySearchTable.NAME + " VARCHAR(50), " +
                Table.HeatCompanySearchTable.HTTP_PORT + " VARCHAR(5)" +
                ")";
        db.execSQL(table);
    }

    /**
     * 建表——保存水表平台管理员选择水司层级记录的表
     *
     * @author LiYuliang
     * @date 2018/4/9 15:23
     */
    private void createTableForHierarchySearch(SQLiteDatabase db) {
        String table = "CREATE TABLE IF NOT EXISTS " +
                Table.HierarchySearchTable.TABLE_NAME +
                " (" +
                Table.HierarchySearchTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Table.HierarchySearchTable.NAME + " VARCHAR(50), " +
                Table.HierarchySearchTable.FIELD_NAME + " VARCHAR(50), " +
                Table.HierarchySearchTable.FIELD_VALUE + " VARCHAR(5)" +
                ")";
        db.execSQL(table);
    }

    /**
     * 打开数据库
     *
     * @return MySQLiteOpenHelper对象
     */
    public DbHelper open() {
        db = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * 关闭数据库
     */
    @Override
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    /**
     * 插入数据
     * delete()方法接收三个参数，第一个参数同样是表名，第二和第三个参数用于指定删除哪些行，对应了SQL语句中的where部分
     */
    public long insert(String tableName, ContentValues values) {
        return db.insert(tableName, null, values);
    }

    /**
     * 删除数据
     * delete()方法接收三个参数，第一个参数同样是表名，第二和第三个参数用于指定删除哪些行，对应了SQL语句中的where部分
     */
    public long delete(String tableName, String whereClause, String[] whereArgs) {
        return db.delete(tableName, whereClause, whereArgs);
    }

    /**
     * 查询数据
     */
    public Cursor findList(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    /**
     * 修改数据
     * update weiboTb set title='heihiehiehieh' where id=2;
     */
    public int update(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(tableName, values, whereClause, whereArgs);
    }

    /**
     * 添加字段
     * 增加一列 - ALTER TABLE 表名 ADD COLUMN 列名 数据类型 限定符
     * db.execSQL("alter table comment add column publishdate integer");
     *
     * @param tableName  表名
     * @param columnName 列名
     * @param columnType 列类型
     */
    public void addColumn(String tableName, String columnName, String columnType) {
        db.execSQL("alter table " + tableName + " add column " + columnName + columnType);
    }

    /**
     * 修改表名
     * 增加一列 - ALTER TABLE 表名 ADD COLUMN 列名 数据类型 限定符
     * db.execSQL("alter table comment add column publishdate integer");
     *
     * @param tableName    表名
     * @param newTableName 新表名
     */
    public void rename(String tableName, String newTableName) {
        db.execSQL("alter table " + tableName + "rename to" + newTableName);
    }

    /**
     * 删除表
     *
     * @param tableName 表名
     */
    public void deleteTable(String tableName) {
        db.delete(tableName, null, null);
    }

    public Cursor exeSql(String sql) {
        return db.rawQuery(sql, null);
    }
}