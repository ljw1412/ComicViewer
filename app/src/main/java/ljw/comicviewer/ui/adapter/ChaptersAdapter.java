package ljw.comicviewer.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
 * 章节按钮gridView适配器
 */

public class ChaptersAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Chapter> chapters;
    private Context context;
    //用map防止滚动图片位置乱跑
    public HashMap<Integer, View> viewMap = new HashMap<>();

    public ChaptersAdapter(Context context, List<Chapter> chapters)
    {
        this.context = context;
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
        chapterViewHolder.name.setText(chapters.get(position).getChapterName());
        if(chapters.get(position).isReadHere()){
            chapterViewHolder.name.setBackgroundResource(R.drawable.shape_border_read_here);
            chapterViewHolder.name.setTextColor(ContextCompat.getColor(context,R.color.white));
        }else{
            chapterViewHolder.name.setBackgroundResource(R.drawable.shape_border_gray);
            chapterViewHolder.name.setTextColor(ContextCompat.getColor(context,R.color.black_shadow));
        }
        return convertView;
    }

    public HashMap<Integer, View> getViewMap(){
        return viewMap;
    }


    public class ChapterViewHolder {
        public TextView name;
    }
}
