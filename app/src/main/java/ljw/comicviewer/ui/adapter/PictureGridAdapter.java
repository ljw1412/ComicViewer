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

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

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
        pictureGridViewHolder.score.setText(comics.get(position).getScore());
        pictureGridViewHolder.update.setText(comics.get(position).getUpdate());
        pictureGridViewHolder.updateStatus.setText(comics.get(position).getUpdateStatus());
        //图片加载代码移动到ComicGridFragment
        loadCover(position,pictureGridViewHolder.image);
        return convertView;
    }

    public void loadCover(final int position, ImageView image){
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.img_load_before)
                .error(R.drawable.img_load_failed)
                .centerCrop()
                .skipMemoryCache(true);

        final WeakReference<ImageView> imageViewWeakReference = new WeakReference<>(image);
        final ImageView target = imageViewWeakReference.get();
        if (target != null) {
            Glide.with(context)
                    .asBitmap()
                    .load(comics.get(position).getImageUrl())
                    .apply(options)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            target.setImageBitmap(resource);
                        }
                    });
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


