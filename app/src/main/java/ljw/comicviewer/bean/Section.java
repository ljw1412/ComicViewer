package ljw.comicviewer.bean;

import java.util.List;

/**
 * Created by ljw on 2017-12-13 013.
 */
//板块bean
public class Section {
    private String title;//板块的标题
    private List<Comic> comics; //板块内的漫画

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Comic> getComics() {
        return comics;
    }

    public void setComics(List<Comic> comics) {
        this.comics = comics;
    }

    @Override
    public String toString() {
        return "Section{" +
                "title='" + title + '\'' +
                ", comics=" + comics +
                '}';
    }
}
