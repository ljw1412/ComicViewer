package ljw.comicviewer.bean;

/**
 * Created by ljw on 2018-01-19 019.
 */

public class Theme {
    private String name;
    private int color;
    private boolean checked = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
