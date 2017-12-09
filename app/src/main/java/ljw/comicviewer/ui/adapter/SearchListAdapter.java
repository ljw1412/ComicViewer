package ljw.comicviewer.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.ui.DetailsActivity;

/**
 * Created by ljw on 2017-09-24 024.
 */

public class SearchListAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater inflater;
    private List<Comic> comics = new ArrayList<>();
    //用map防止滚动图片位置乱跑
    private HashMap<Integer, View> viewMap = new HashMap<>();

    public SearchListAdapter(Context context, List<Comic> comics) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.comics = comics;
    }

    @Override
    public int getCount() {
        return comics.size();
    }

    @Override
    public Object getItem(int position) {
        return comics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        SearchViewHolder searchViewHolder;
        if (!viewMap.containsKey(position) || viewMap.get(position) == null){
            convertView = inflater.inflate(R.layout.item_search, null);
            searchViewHolder = new SearchViewHolder(convertView);
            convertView.setTag(searchViewHolder);
            viewMap.put(position, convertView);
        } else  {
            convertView = viewMap.get(position);
            searchViewHolder = (SearchViewHolder) convertView.getTag();
        }
        searchViewHolder.name.setText(comics.get(position).getName());
        searchViewHolder.author.setText(comics.get(position).getAuthor());
        searchViewHolder.update.setText(comics.get(position).getUpdate());
        searchViewHolder.type.setText(comics.get(position).getTag());
        searchViewHolder.info.setText(comics.get(position).getInfo());
        searchViewHolder.updateStatus.setText(comics.get(position).getUpdateStatus());
        searchViewHolder.end.setText(comics.get(position).isEnd()?"已完结":"连载中");
        searchViewHolder.end.setTextColor(comics.get(position).isEnd()?Color.rgb(236,19,111):Color.rgb(68,221,0));
        //图片加载
        loadCover(position,searchViewHolder.image);
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

    public void go(int position){
        Comic comic = comics.get(position);
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("id",comic.getComicId());
        intent.putExtra("score",comic.getScore());
        intent.putExtra("title",comic.getName());
        context.startActivity(intent);
    }

    class SearchViewHolder
    {
        @BindView(R.id.search_body)
        LinearLayout body;
        @BindView(R.id.search_cover)
        ImageView image;
        @BindView(R.id.search_comic_name)
        TextView name;
        @BindView(R.id.search_comic_author)
        TextView author;
        @BindView(R.id.search_comic_update)
        TextView update;
        @BindView(R.id.search_comic_update_status)
        TextView updateStatus;
        @BindView(R.id.search_comic_type)
        TextView type;
        @BindView(R.id.search_comic_info)
        TextView info;
        @BindView(R.id.search_comic_is_end)
        TextView end;

        public SearchViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
