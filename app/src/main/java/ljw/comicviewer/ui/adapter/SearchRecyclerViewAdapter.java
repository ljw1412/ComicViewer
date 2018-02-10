package ljw.comicviewer.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.ref.WeakReference;
import java.util.List;

import ljw.comicviewer.R;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.ui.listeners.OnItemClickListener;
import ljw.comicviewer.ui.listeners.OnItemLongClickListener;

/**
 * Created by ljw on 2018-02-10 010.
 */

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchItemHolder>{
    private Context context;
    private LayoutInflater inflater;
    private List<Comic> comics;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public SearchRecyclerViewAdapter(Context context, List<Comic> comics) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.comics = comics;
    }

    @Override
    public SearchItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_search, parent,false);
        return new SearchItemHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchItemHolder holder, final int position) {
        holder.name.setText(comics.get(position).getName());
        holder.author.setText(comics.get(position).getAuthor());
        holder.update.setText(comics.get(position).getUpdate());
        holder.type.setText(comics.get(position).getTag());
        holder.info.setText(comics.get(position).getInfo());
        holder.updateStatus.setText(comics.get(position).getUpdateStatus());
        holder.end.setText(comics.get(position).isEnd()?"已完结":"连载中");
        holder.end.setTextColor(comics.get(position).isEnd()? Color.rgb(236,19,111):Color.rgb(68,221,0));
        //图片加载
        loadCover(position,holder.image);
        if(onItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.OnItemClick(view,position);
                }
            });
        }

        if (onItemLongClickListener!=null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return onItemLongClickListener.onItemLongClick(view,position);
                }
            });
        }
    }

    public void loadCover(final int position, ImageView image){
        RequestOptions options = new RequestOptions();
        options.placeholder(R.color.transparent)
                .error(R.drawable.img_load_failed)
                .centerCrop()
                .skipMemoryCache(true);

        final WeakReference<ImageView> imageViewWeakReference = new WeakReference<>(image);
        final ImageView target = imageViewWeakReference.get();
        if (target != null && !((Activity) context).isDestroyed()) {
            Glide.with(context)
                    .asBitmap()
                    .load(comics.get(position).getImageUrl())
                    .apply(options)
                    .into(target);
        }
    }

    @Override
    public int getItemCount() {
        return comics != null ? comics.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
