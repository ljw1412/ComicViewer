package ljw.comicviewer.bean;

/**
 * Created by ljw on 2017-12-07 007.
 */

public class Category {
    private String parentName;//父类型名
    private String name;//子类型名
    private String value;//子类型值
    private boolean selected = false;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
