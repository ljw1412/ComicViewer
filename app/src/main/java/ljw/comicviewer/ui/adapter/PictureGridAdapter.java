package ljw.comicviewer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.R;

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
        ViewHolder viewHolder;
        if (!viewMap.containsKey(position) || viewMap.get(position) == null){
            convertView = inflater.inflate(R.layout.item_comic_grid, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            viewMap.put(position, convertView);
        } else  {
            convertView = viewMap.get(position);
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(comics.get(position).getName());
        viewHolder.score.setText(comics.get(position).getScore());
        viewHolder.update.setText(comics.get(position).getUpdate());
        viewHolder.updateStatus.setText(comics.get(position).getUpdateStatus());

        //设置封面
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.img_load_before);
        options.error(R.drawable.img_load_failed);
        //禁用缓存
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true);
        Glide.with(context)
                .asBitmap()
                .load(comics.get(position).getImageUrl())
                .apply(options)
                .into(viewHolder.image);
//            if(comics.get(position).getCover()!=null){
//                //加载图片的同时显示连载情况
//                viewHolder.image.setImageBitmap(comics.get(position).getCover());
//                viewHolder.isEnd.setImageResource(comics.get(position).isEnd()?R.drawable.state_finish:R.drawable.state_serialise);
//            }else if (imageState.get(position)!=null && imageState.get(position)==1){
//                //如果封面为null且为加载完成状态，认为加载失败
//                viewHolder.image.setImageResource(R.drawable.img_load_failed);
//            }else{
//                //图片加载中
//                viewHolder.image.setImageResource(R.drawable.img_load_before);
//            }
        return convertView;
    }
}
class ViewHolder
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

    public ViewHolder(View view) {
        ButterKnife.bind(this,view);
    }
}

