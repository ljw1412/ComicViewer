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
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ljw.comicviewer.others.MyGridView;
import ljw.comicviewer.R;
import ljw.comicviewer.ui.ReadViewerLoadingActivity;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.bean.Chapter;

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
            Chapter chapter = chapters.get(position);
            Log.d(TAG,chapters.get(position).getChapter_name());
            Intent intent = new Intent(context,ReadViewerLoadingActivity.class);
            intent.putExtra("comic_id",chapter.getComic_id());
            intent.putExtra("comic_name",comicName);
            intent.putExtra("chapter_id",chapter.getChapter_id());
            intent.putExtra("chapter_name",chapter.getChapter_name());
            intent.putExtra("position",1);
            startActivity(intent);
        }
    }

    //自定义适配器
    class ChaptersAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Chapter> chapters;
        //用map防止滚动图片位置乱跑
        private HashMap<Integer, View> viewMap = new HashMap<>();

        public ChaptersAdapter(Context context, List<Chapter> chapters)
        {
            super();
            inflater = LayoutInflater.from(context);
            this.chapters = chapters;
        }

        @Override
        public int getCount()
        {
            return chapters.size();
        }

        @Override
        public Object getItem(int i) {
            return chapters.get(i);
        }


        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ViewHolder viewHolder;
            if (!viewMap.containsKey(position) || viewMap.get(position) == null){
                convertView = inflater.inflate(R.layout.item_chapters, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.chapters_name);
                convertView.setTag(viewHolder);
                viewMap.put(position, convertView);
            } else  {
                convertView = viewMap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(chapters.get(position).getChapter_name());
            return convertView;
        }
    }
    class ViewHolder
    {
        public TextView name;
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

}
