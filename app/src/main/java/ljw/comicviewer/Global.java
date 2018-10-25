package ljw.comicviewer;

/**
 * 全局常量
 */

public class Global {
    //请求标识
    public static final String REQUEST_COMICS_LIST = "GET_COMICS_LIST";//获得漫画列表
    public static final String REQUEST_HOME = "REQUEST_HOME";//获得主页
    public static final String REQUEST_COMIC_FILTER = "REQUEST_COMIC_FILTER";
    public static final String REQUEST_COMIC_NEWADD = "REQUEST_COMIC_NEWADD";//获得新上架漫画
    public static final String REQUEST_COMICS_UPDATE = "REQUEST_COMICS_UPDATE";//获得最新漫画
    public static final String REQUEST_COMICS_INFO = "REQUEST_COMICS_INFO";//获得漫画信息
    public static final String REQUEST_COMICS_SEARCH = "REQUEST_COMICS_SEARCH";//获得搜索漫画
    public static final String REQUEST_AUTHOR_COMICS = "REQUEST_AUTHOR_COMICS";//获得作者漫画
    public static final String REQUEST_RULE_LIST = "REQUEST_RULE_LIST"; // 获得规则列表

    public static final int REQUEST_COMIC_HISTORY = 100;//漫画历史记录标识
    public static final int THEME_CHANGE = 101;//换肤标识
    public static final int STATUS_CollectionToDetails = 10001;//从收藏到详细页的标识
    public static final int STATUS_HomeToSetting = 10002; //主页与设置页切换

    public static final int STATUS_COVER_UPDATE = 20001;//封面更新
    //参数
    public static final int SNACKBAR_DURATION = 2000;//SNACKBAR的延迟

    public static final int ITEM_COMIC_VIEW_WIDTH = 120; //漫画宽度DIP
    public static final int ITEM_CHAPTER_VIEW_WIDTH = 80; //章节视图宽度DIP

}
