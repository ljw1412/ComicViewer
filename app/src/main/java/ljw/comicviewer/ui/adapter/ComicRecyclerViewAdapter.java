package ljw.comicviewer.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import ljw.comicviewer.R;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.listeners.OnItemClickListener;
import ljw.comicviewer.ui.listeners.OnItemLongClickListener;
import ljw.comicviewer.util.DensityUtil;

/**
 * Created by ljw on 2018-01-28 028.
 */

public class ComicRecyclerViewAdapter extends RecyclerView.Adapter<ComicItemViewHolder>{
    private Context context;
    private LayoutInflater inflater;
    private List<Comic> comics;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private int viewWidth;

    public ComicRecyclerViewAdapter(Context context, List<Comic> comics, int viewWidth) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.comics = comics;
        this.viewWidth = viewWidth;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public ComicItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_comic_grid,parent,false);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = viewWidth;
        int padding = DensityUtil.dp2px(context,2);
        view.setPadding(padding,padding,padding,padding);
        view.setLayoutParams(lp);
        view.setBackgroundResource(R.drawable.selector_bg_null_to_black_pressed);
        ComicItemViewHolder filterItemViewHolder = new ComicItemViewHolder(view);
        return filterItemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ComicItemViewHolder holder, final int position) {
        holder.name.setText(comics.get(position).getName());
        if(comics.get(position).getScore()!=null) {
            holder.score.setText(comics.get(position).getScore());
        }else{
            holder.score.setVisibility(View.GONE);
        }
        if(comics.get(position).getUpdate()!=null) {
            holder.update.setText(comics.get(position).getUpdate());
        }else{
            holder.update.setVisibility(View.GONE);
        }
        holder.updateStatus.setText(comics.get(position).getUpdateStatus());

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

        loadCover(position,holder);
    }

    @Override
    public void onViewRecycled(ComicItemViewHolder holder) {
        if(holder!=null){
//            holder.image.setImageResource(0);
//            holder.image.setImageBitmap(null);
            holder.isEnd.setImageResource(0);
            Glide.with(context).clear(holder.image);
        }
        super.onViewRecycled(holder);
    }

    public void loadCover(final int position, final ComicItemViewHolder holder) {
        if (holder != null) {
             if ( comics.size() > 0 && !((Activity) context).isDestroyed()) {
                RequestOptions options = new RequestOptions();
                options.placeholder(R.color.transparent)
                        .error(R.drawable.img_load_failed)
                        .centerCrop()
                        .transform(new RoundedCorners(30))
                        .skipMemoryCache(true);
                Glide.with(context)
                        .asBitmap()
                        .load(comics.get(position).getImageUrl())
                        .apply(options)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                String no_end_info = null;
                                if (RuleStore.get().getConfigRule() != null) {
                                    no_end_info = RuleStore.get().getConfigRule().get("no-end-info");
                                }
                                if (no_end_info == null || no_end_info.equals("false")) {
                                    holder.isEnd.setImageResource(
                                            comics.get(position).isEnd() ?
                                                    R.drawable.state_finish : R.drawable.state_serialise);
                                } else {
                                    holder.isEnd.setImageResource(0);
                                }
                                return false;
                            }
                        })
                        .into(holder.image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return comics != null ? comics.size() : 0;
    }

    public void remove(int position){
        comics.remove(position);
        notifyDataSetChanged();
    }

    public void remove(String comicId){
        for(int i = 0 ; i< comics.size();i++){
            if(comics.get(i).getComicId().equals(comicId)){
                remove(i);
                break;
            }
        }
    }
}
