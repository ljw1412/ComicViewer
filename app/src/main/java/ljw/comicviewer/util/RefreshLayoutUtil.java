package ljw.comicviewer.util;

import com.scwang.smartrefresh.layout.api.RefreshLayout;

/**
 * 刷新界面工具类
 */

public class RefreshLayoutUtil {
    public final class Mode{
        public static final int Disable = -1;
        public static final int Both = 0;
        public static final int Only_Refresh = 1;
        public static final int Only_LoadMore = 2;
    }

    //设置模式
    public static void setMode(RefreshLayout refreshLayout,int mode){
        switch (mode){
            case Mode.Disable:
                refreshLayout.setEnableRefresh(false);
                refreshLayout.setEnableLoadmore(false);
                break;
            case Mode.Both:
                refreshLayout.setEnableRefresh(true);
                refreshLayout.setEnableLoadmore(true);
                break;
            case Mode.Only_LoadMore:
                refreshLayout.setEnableRefresh(false);
                refreshLayout.setEnableLoadmore(true);
                break;
            case Mode.Only_Refresh:
                refreshLayout.setEnableRefresh(true);
                refreshLayout.setEnableLoadmore(false);
                break;
        }
    }

    //完成刷新或加载
    public static void onFinish(RefreshLayout refreshLayout){
        if (refreshLayout.isRefreshing()){
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.isLoading()){
            refreshLayout.finishLoadmore();
        }
    }
}
