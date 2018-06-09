package cn.njmeter.constantflowvalve.bean;

/**
 * 水司层级的实体类
 * Created by LiYuliang on 2017/4/8 0008.
 *
 * @author LiYuliang
 * @version 2017/11/20
 */

public class Hierarchy {

    public final static int SUPPLIER = 0;
    public final static int EXCHANGE_STATION = 1;
    public final static int VILLAGE = 2;
    public final static int BUILDING = 3;
    public final static int ENTRANCE = 4;

    private int id;
    private int type;
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object obj) {
        //先检查是否其自反性，后比较obj是否为空。这样效率高
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Hierarchy)) {
            return false;
        }
        final Hierarchy hierarchy = (Hierarchy) obj;
        if (getId() != hierarchy.getId()) {
            return false;
        }
        if (getType() != hierarchy.getType()) {
            return false;
        }
        return getText().equals(hierarchy.getText()) || getText().equals(hierarchy.getText());
    }
}
