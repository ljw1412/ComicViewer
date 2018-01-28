package ljw.comicviewer.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Date;
import java.util.List;

import ljw.comicviewer.R;
import ljw.comicviewer.bean.History;
import ljw.comicviewer.db.HistoryHolder;
import ljw.comicviewer.ui.listeners.OnItemClickListener;

/**
 * Created by ljw on 2017-12-04 004.
 */

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryItemViewHolder>{
    private Context context;
    private LayoutInflater inflater;
    private List list;
    private OnItemClickListener onItemClickListener;

    public HistoryRecyclerViewAdapter(Context context, List list) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onViewRecycled(HistoryItemViewHolder holder) {
        if (holder!=null){
            Glide.with(context).clear(holder.image);
        }
        super.onViewRecycled(holder);
    }

    @Override
    public HistoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_history,parent,false);
        HistoryItemViewHolder historyItemViewHolder = new HistoryItemViewHolder(v);
        return historyItemViewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryItemViewHolder holder, final int position) {
        if (getItemCount()<=0) return;
        if(list.get(position) instanceof History){
            final History history = (History) list.get(position);
            holder.comicName.setText(history.getComicName());
            holder.chapterName.setText("进度:"+history.getChapterName());
            holder.page.setText("第"+history.getPage()+"页");
            holder.readTime.setText("时间:"+DateFormat.format("yyyy-MM-dd HH:mm:ss",new Date(history.getReadTime())));
            holder.end.setText(history.isEnd()?"已完结":"连载中");
            holder.end.setTextColor(history.isEnd()? Color.rgb(236,19,111):Color.rgb(68,221,0));
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.img_load_before)
                    .error(R.drawable.img_load_failed).centerCrop();
            Glide.with(context).load(history.getImgUrl()).apply(options).into(holder.image);

            View view = holder.view;
            if(onItemClickListener!=null){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.OnItemClick(view,position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void remove(int position){
        History history = (History) list.get(position);
        Log.d("HistoryRVAdapter", "remove: "+history.getComicName());
        HistoryHolder historyHolder = new HistoryHolder(context);
        list.remove(position);
        historyHolder.delOneHistory(history.getComicId());
        notifyItemRemoved(position);
    }
}
