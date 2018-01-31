package ljw.comicviewer.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import ljw.comicviewer.R;

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

    public static void init(Context context, RefreshLayout refreshLayout,
                                   int mode,boolean autoLoadMore){
        //设置下拉模式
        RefreshLayoutUtil.setMode(refreshLayout, mode);
        //设置主题色
        refreshLayout.setPrimaryColors(
                ThemeUtils.getColorById(context, R.color.theme_color_primary),
                ContextCompat.getColor(context,R.color.window_background));
        //下拉到底最后是否自动加载，否：需要再拉一下
        refreshLayout.setEnableAutoLoadmore(autoLoadMore);
        //不在加载更多完成之后滚动内容显示新数据
        refreshLayout.setEnableScrollContentWhenLoaded(false);
    }
}
