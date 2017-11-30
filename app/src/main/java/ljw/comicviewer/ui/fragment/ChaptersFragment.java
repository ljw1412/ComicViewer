package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Chapter;
import ljw.comicviewer.bean.History;
import ljw.comicviewer.db.HistoryHolder;
import ljw.comicviewer.others.MyGridView;
import ljw.comicviewer.store.ComicReadStore;
import ljw.comicviewer.ui.ComicReaderActivity;
import ljw.comicviewer.ui.adapter.ChaptersAdapter;
import ljw.comicviewer.util.DisplayUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChaptersFragment extends Fragment  {
    private String TAG = ChaptersFragment.class.getSimpleName()+"----";
    private Context context;
    private MyGridView grid_chapters_list;
    private ChaptersAdapter chaptersAdapter;
    private List<Chapter> chapters = new ArrayList<>();
    private String comicName;

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public ChaptersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = this.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_chapters,null);
        grid_chapters_list = (MyGridView) rootView.findViewById(R.id.details_chapters_list);

//        //根据屏幕宽度设置列数
        int columns = DisplayUtil.getGridNumColumns(context,80);
        grid_chapters_list.setNumColumns(columns);
        chaptersAdapter = new ChaptersAdapter(context,chapters);
        grid_chapters_list.setAdapter(chaptersAdapter);
        grid_chapters_list.setOnItemClickListener(new ItemClickListener());

        return rootView;
    }

    //TODO:网格对象点击事件
    class  ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //将同类型的全部章节存入store
            ComicReadStore.get().setObj(chapters);
            ComicReadStore.get().setCurrentIndex(position);

            Chapter chapter = chapters.get(position);
            Log.d(TAG,"加载章节:"+chapter.getChapterName());
            Intent intent = new Intent(context,ComicReaderActivity.class);
            intent.putExtra("comic_id",chapter.getComicId());
            intent.putExtra("comic_name",comicName);
            intent.putExtra("chapter_id",chapter.getChapterId());
            intent.putExtra("chapter_name",chapter.getChapterName());
            intent.putExtra("position",chapter.getPage());
            startActivityForResult(intent, Global.REQUEST_COMIC_HISTORY);
        }
    }

    public void continueReadingClick(int position){
        HashMap<Integer, View> map = chaptersAdapter.getViewMap();
        if(map.size()>0){
//            View view = map.get(position);
//            if(view!=null){
            try {
                grid_chapters_list.performItemClick(null,position,0);
            } catch (Exception e) {
                Log.e(TAG, "continueReadingClick: 模拟点击事件异常");
            }
//            }
        }
    }

    public void addChapters(List<Chapter> chapters) {
        this.chapters.addAll(chapters);
        Log.d(TAG, "addChapters: add:"+chaptersAdapter.getCount());
        chaptersAdapter.notifyDataSetChanged();
    }

    public void clearChapters() {
        this.chapters.clear();
        chaptersAdapter.notifyDataSetChanged();
    }

    //更新读到的位置
    public void updateChapters(){
        if(chapters.size()>0) {
            //查询历史记录
            HistoryHolder historyHolder = new HistoryHolder(context);
            History history = historyHolder.getHistory(chapters.get(0).getComicId());
            Log.d(TAG, "历史记录: "+(history != null ? history.toString():"还没有看过"));
            for (Chapter chapter : chapters) {
                if (history != null && chapter.getChapterId().equals(history.getChapterId())) {
                    chapter.setReadHere(true);
                    chapter.setPage(history.getPage());
                }else if (chapter.isReadHere()){
                    chapter.setReadHere(false);
                    chapter.setPage(1);
                }
            }
            chaptersAdapter.notifyDataSetChanged();
        }
    }
}
