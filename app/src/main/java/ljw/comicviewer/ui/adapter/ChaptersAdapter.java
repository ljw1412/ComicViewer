package ljw.comicviewer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import ljw.comicviewer.R;
import ljw.comicviewer.bean.Chapter;

/**
 * Created by ljw on 2017-09-19 019.
 */

public class ChaptersAdapter extends BaseAdapter {
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
        ChapterViewHolder chapterViewHolder;
        if (!viewMap.containsKey(position) || viewMap.get(position) == null){
            convertView = inflater.inflate(R.layout.item_chapters, null);
            chapterViewHolder = new ChapterViewHolder();
            chapterViewHolder.name = (TextView) convertView.findViewById(R.id.chapters_name);
            convertView.setTag(chapterViewHolder);
            viewMap.put(position, convertView);
        } else  {
            convertView = viewMap.get(position);
            chapterViewHolder = (ChapterViewHolder) convertView.getTag();
        }
        chapterViewHolder.name.setText(chapters.get(position).getChapter_name());
        return convertView;
    }
}
class ChapterViewHolder {
    public TextView name;
}
