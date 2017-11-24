package ljw.comicviewer.bean;

/**
 * Created by ljw on 2017-11-24 024.
 */

public class Author {
    private String name;
    private String mark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", mark='" + mark + '\'' +
                '}';
    }
}
