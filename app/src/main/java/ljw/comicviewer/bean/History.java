package ljw.comicviewer.bean;

/**
 * Created by ljw on 2017-11-29 029.
 */

public class History {
    private String comicId;//漫画id
    private String comicName;//漫画名
    private String imgUrl;
    private String chapterId;//章节id
    private String chapterName;//章节名称
    private boolean isEnd;
	private int page;
    private long readTime;
    private String comeFrom;

    public String getComicId() {
        return comicId;
    }

    public void setComicId(String comicId) {
        this.comicId = comicId;
    }

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    @Override
    public String toString() {
        return "History{" +
                "comicId='" + comicId + '\'' +
                ", comicName='" + comicName + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", chapterId='" + chapterId + '\'' +
                ", chapterName='" + chapterName + '\'' +
                ", isEnd=" + isEnd +
                ", page=" + page +
                ", readTime=" + readTime +
                ", comeFrom='" + comeFrom + '\'' +
                '}';
    }
}
