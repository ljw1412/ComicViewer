package ljw.comicviewer.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.lang.ref.WeakReference;
import java.util.List;

import ljw.comicviewer.R;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.listeners.OnItemClickListener;
import ljw.comicviewer.util.DensityUtil;

/**
 * Created by ljw on 2018-01-28 028.
 */

public class FilterRecyclerViewAdapter extends RecyclerView.Adapter<FilterItemViewHolder>{
    private Context context;
    private LayoutInflater inflater;
    private List<Comic> comics;
    private OnItemClickListener onItemClickListener;
    private int viewWidth;

    public FilterRecyclerViewAdapter(Context context, List<Comic> comics,int viewWidth) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.comics = comics;
        this.viewWidth = viewWidth;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public FilterItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_comic_grid,parent,false);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = viewWidth;
        int padding = DensityUtil.dp2px(context,2);
        view.setPadding(padding,padding,padding,padding);
        view.setLayoutParams(lp);
        view.setBackgroundResource(R.drawable.selector_bg_null_to_black_pressed);
        FilterItemViewHolder filterItemViewHolder = new FilterItemViewHolder(view);
        return filterItemViewHolder;
    }

    @Override
    public void onBindViewHolder(final FilterItemViewHolder holder, final int position) {
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
        loadCover(position,holder);
    }

    @Override
    public void onViewRecycled(FilterItemViewHolder holder) {
        if(holder!=null){
            Glide.with(context).clear(holder.image);
        }
        super.onViewRecycled(holder);
    }

    public void loadCover(final int position, final FilterItemViewHolder holder) {
        if (holder != null) {
            holder.image.setImageResource(0);
            holder.image.setImageBitmap(null);
            holder.isEnd.setImageResource(0);
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.img_load_before)
                    .error(R.drawable.img_load_failed)
                    .centerCrop()
                    .skipMemoryCache(true);
            final WeakReference<ImageView> imageViewWeakReference = new WeakReference<>(holder.image);
            final ImageView target = imageViewWeakReference.get();
            if (target != null && comics.size() > 0 && !((Activity) context).isDestroyed()) {
                Glide.with(context)
                        .asBitmap()
                        .load(comics.get(position).getImageUrl())
                        .apply(options)
                        .into(new SimpleTarget<Bitmap>() {


                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                target.setImageBitmap(resource);
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
                            }
                        });
            }
        }
    }

    @Override
    public int getItemCount() {
        return comics != null ? comics.size() : 0;
    }
}
