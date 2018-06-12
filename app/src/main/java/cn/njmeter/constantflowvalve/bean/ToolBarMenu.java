package cn.njmeter.constantflowvalve.bean;
/**
 * Created by sfan on 2017/5/23.
 * TooBar 菜单
 */

public class ToolBarMenu {

    public String id;
    public int img;
    public String name;
    public int count = 0;

    public ToolBarMenu(String id, int img, String name) {
        super();
        this.id = id;
        this.img = img;
        this.name = name;
    }

//    public ToolBarMenu(String id, int img, int str) {
//        super();
//        this.id = id;
//        this.img = img;
//        this.name = BaseApplication.getInstance().getString(str);
//    }

    public ToolBarMenu setCount(int count) {
        this.count = count;
        return this;
    }

}

