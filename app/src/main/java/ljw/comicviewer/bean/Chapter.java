package ljw.comicviewer.bean;

/**
 * Created by ljw on 2017-09-03 003.
 */

public class Chapter {
    private String comic_id;
    private String chapter_id;
    private String Chapter_name;
    private int type;//0单行本,1单话，2其他

    public String getComic_id() {
        return comic_id;
    }

    public void setComic_id(String comic_id) {
        this.comic_id = comic_id;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getChapter_name() {
        return Chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        Chapter_name = chapter_name;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "comic_id='" + comic_id + '\'' +
                ", chapter_id='" + chapter_id + '\'' +
                ", Chapter_name='" + Chapter_name + '\'' +
                ", type=" + type +
                '}';
    }
}
