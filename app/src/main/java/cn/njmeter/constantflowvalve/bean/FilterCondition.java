package cn.njmeter.constantflowvalve.bean;

/**
 * 查询水表末次数据时传给后台的筛选条件实体类
 * Created by LiYuliang on 2018/1/3.
 *
 * @author LiYuliang
 * @version 2018/1/3
 */

public class FilterCondition {

    /**
     * 筛选条件
     */
    private String contentFiltering;
    /**
     * 比较符
     */
    private String comparisonOperators;
    /**
     * 比较值
     */
    private String valueFiltering;
    /**
     * 筛选条件在标签列表的位置
     */
    private int contentFilteringPosition;
    /**
     * 比较符在标签列表的位置
     */
    private int comparisonOperatorsPosition;
    /**
     * 比较值在标签列表的位置（如果是-1则表示是手动输入的）
     */
    private int valueFilteringPosition;
    /**
     * 是否含有自定义的值
     */
    private boolean hasCustomValue;

    public String getContentFiltering() {
        return contentFiltering;
    }

    public void setContentFiltering(String contentFiltering) {
        this.contentFiltering = contentFiltering;
    }

    public String getComparisonOperators() {
        return comparisonOperators;
    }

    public void setComparisonOperators(String comparisonOperators) {
        this.comparisonOperators = comparisonOperators;
    }

    public String getValueFiltering() {
        return valueFiltering;
    }

    public void setValueFiltering(String valueFiltering) {
        this.valueFiltering = valueFiltering;
    }

    public int getContentFilteringPosition() {
        return contentFilteringPosition;
    }

    public void setContentFilteringPosition(int contentFilteringPosition) {
        this.contentFilteringPosition = contentFilteringPosition;
    }

    public int getComparisonOperatorsPosition() {
        return comparisonOperatorsPosition;
    }

    public void setComparisonOperatorsPosition(int comparisonOperatorsPosition) {
        this.comparisonOperatorsPosition = comparisonOperatorsPosition;
    }

    public int getValueFilteringPosition() {
        return valueFilteringPosition;
    }

    public void setValueFilteringPosition(int valueFilteringPosition) {
        this.valueFilteringPosition = valueFilteringPosition;
    }

    public boolean isHasCustomValue() {
        return hasCustomValue;
    }

    public void setHasCustomValue(boolean hasCustomValue) {
        this.hasCustomValue = hasCustomValue;
    }
}
