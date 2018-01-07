package ljw.comicviewer.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.store.RuleStore;

/**
 * Created by ljw on 2017-09-10 010.
 */

public class PictureGridAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Comic> comics;
    //用map防止滚动图片位置乱跑
    private HashMap<Integer, View> viewMap = new HashMap<>();

    public PictureGridAdapter(Context context, List<Comic> comics)
    {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.comics = comics;
    }

    @Override
    public int getCount()
    {
        return comics.size();
    }

    @Override
    public Object getItem(int i) {
        return comics.get(i);
    }


    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        PictureGridViewHolder pictureGridViewHolder;
        if (!viewMap.containsKey(position) || viewMap.get(position) == null){
            convertView = inflater.inflate(R.layout.item_comic_grid, null);
            pictureGridViewHolder = new PictureGridViewHolder(convertView);
            convertView.setTag(pictureGridViewHolder);
            viewMap.put(position, convertView);
        } else  {
            convertView = viewMap.get(position);
            pictureGridViewHolder = (PictureGridViewHolder) convertView.getTag();
        }
        pictureGridViewHolder.name.setText(comics.get(position).getName());
        if(comics.get(position).getScore()!=null) {
            pictureGridViewHolder.score.setText(comics.get(position).getScore());
        }else{
            pictureGridViewHolder.score.setVisibility(View.GONE);
        }
        if(comics.get(position).getUpdate()!=null) {
            pictureGridViewHolder.update.setText(comics.get(position).getUpdate());
        }else{
            pictureGridViewHolder.update.setVisibility(View.GONE);
        }
        pictureGridViewHolder.updateStatus.setText(comics.get(position).getUpdateStatus());
        //图片加载代码移动到ComicGridFragment
//        loadCover(position,pictureGridViewHolder.image);
        return convertView;
    }

    public void loadCover(final int position, View view){
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.img_load_before)
                .error(R.drawable.img_load_failed)
                .centerCrop()
                .skipMemoryCache(true);
        final PictureGridViewHolder viewHolder = (PictureGridViewHolder) view.getTag();
        if(viewHolder != null){
            final WeakReference<ImageView> imageViewWeakReference = new WeakReference<>(viewHolder.image);
            final ImageView target = imageViewWeakReference.get();
            if (target != null && comics.size()>0) {
                Glide.with(context)
                        .asBitmap()
                        .load(comics.get(position).getImageUrl())
                        .apply(options)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                target.setImageBitmap(resource);
                                String no_end_info = "false";
                                try {
                                    no_end_info = RuleStore.get().getConfigRule().get("no-end-info");
                                } catch (Exception e) {}
                                if(no_end_info.equals("false")) {
                                    viewHolder.isEnd.setImageResource(
                                            comics.get(position).isEnd() ?
                                                    R.drawable.state_finish : R.drawable.state_serialise);
                                }else{
                                    viewHolder.isEnd.setImageResource(0);
                                }
                            }
                        });
            }
        }
    }

    public HashMap<Integer, View> getViewMap() {
        return viewMap;
    }


    public class PictureGridViewHolder
    {
        @BindView(R.id.comic_score)
        TextView score;
        @BindView(R.id.comic_updateDate)
        TextView update;
        @BindView(R.id.comic_updateStatus)
        TextView updateStatus;
        @BindView(R.id.comic_name)
        TextView name;
        @BindView(R.id.comic_img)
        ImageView image;
        @BindView(R.id.comic_status)
        ImageView isEnd;

        public PictureGridViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}


