package ljw.comicviewer;

/**
 * Created by ljw on 2017-08-31 031.
 */

public class Global {
    //请求标识
    public static final String REQUEST_COMICS_LIST = "GET_COMICS_LIST";//获得漫画列表
    public static final String REQUEST_HOME = "REQUEST_HOME";//获得主页
    public static final String REQUEST_COMIC_NEWADD = "REQUEST_COMIC_NEWADD";//获得新上架漫画
    public static final String REQUEST_COMICS_UPDATE = "REQUEST_COMICS_UPDATE";//获得最新漫画
    public static final String REQUEST_COMICS_INFO = "REQUEST_COMICS_INFO";//获得漫画信息
    public static final String REQUEST_COMICS_SEARCH = "REQUEST_COMICS_SEARCH";//获得搜索漫画
    public static final String REQUEST_AUTHOR_COMICS = "REQUEST_AUTHOR_COMICS";//获得作者漫画

    public static final int REQUEST_COMIC_HISTORY = 100;//漫画历史记录标识
    public static final int CollectionToDetails = 10001;//从收藏到详细页的标识
    //参数
    public static final int SNACKBAR_DURATION = 2000;//SNACKBAR的延迟
}
