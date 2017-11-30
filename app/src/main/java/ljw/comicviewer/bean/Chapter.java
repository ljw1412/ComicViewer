package ljw.comicviewer.bean;

/**
 * Created by ljw on 2017-09-03 003.
 */

public class Chapter {
    private String comicId;
    private String chapterId;
    private String ChapterName;
    private int type;//0单行本,1单话，2其他
    private boolean readHere = false;
    private int page = 1;

    public String getComicId() {
        return comicId;
    }

    public void setComicId(String comicId) {
        this.comicId = comicId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapter_id) {
        this.chapterId = chapter_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getChapterName() {
        return ChapterName;
    }

    public void setChapterName(String chapterName) {
        ChapterName = chapterName;
    }

    public boolean isReadHere() {
        return readHere;
    }

    public void setReadHere(boolean readHere) {
        this.readHere = readHere;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "comicId='" + comicId + '\'' +
                ", chapter_id='" + chapterId + '\'' +
                ", ChapterName='" + ChapterName + '\'' +
                ", type=" + type +
                ", readHere=" + readHere +
                ", page=" + page +
                '}';
    }
}
