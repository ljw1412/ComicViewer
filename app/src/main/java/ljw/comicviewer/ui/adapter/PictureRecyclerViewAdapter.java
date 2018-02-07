package ljw.comicviewer.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import ljw.comicviewer.R;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.util.AreaClickHelper;

/**
 * Created by ljw on 2018-02-05 005.
 */

public class PictureRecyclerViewAdapter extends RecyclerView.Adapter<PictureItemHolder>{
    private String TAG = getClass().getSimpleName()+"----";
    private Context context;
    private List<String> imgUrls;
    private RuleStore ruleStore = RuleStore.get();
    private AreaClickHelper areaClickHelper;

    public PictureRecyclerViewAdapter(Context context, List<String> imgUrls) {
        this.context = context;
        this.imgUrls = imgUrls;
        areaClickHelper = new AreaClickHelper(context);
    }

    @Override
    public void onViewRecycled(PictureItemHolder holder) {
        if (holder!=null){
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.itemView.setLayoutParams(lp);
            Glide.with(context).clear(holder.ivPicture);
        }
        super.onViewRecycled(holder);
    }


    @Override
    public PictureItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_read_viewer_vertical,parent,false);
        return new PictureItemHolder(v);
    }

    @Override
    public void onBindViewHolder(final PictureItemHolder holder, int position) {
        if (getItemCount()<=0) return;
        final String url = imgUrls.get(position);
        holder.txtPageNum.setText((position+1)+"");
        holder.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage(holder,url,true);
            }
        });
        loadImage(holder,url,true);
    }

    @Override
    public int getItemCount() {
        return imgUrls != null ? imgUrls.size() : 0;
    }

    public void loadImage(final PictureItemHolder holder, final String url, final boolean first){
//        final ImageView pic = holder.ivPicture;
//        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(pic);

//        final AreaClickHelper areaClickHelper = viewPager.getAreaClickHelper();
        holder.txtPageNum.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.btnRefresh.setVisibility(View.GONE);

        RequestOptions options = new RequestOptions();
        if(!((Activity) context).isDestroyed())
        Glide.with(context)
                .asBitmap()
                .load(new GlideUrl(url
                        ,new LazyHeaders.Builder().addHeader("Referer",ruleStore.getHost()).build()
                ))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e(TAG, "onLoadFailed: "+url+"加载失败！");
                        if (!first || ruleStore.getReadRule().get("imghost")==null) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.btnRefresh.setVisibility(View.VISIBLE);
                        }else {
                            loadImageAgain(holder,url);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.btnRefresh.setVisibility(View.GONE);
                        holder.txtPageNum.setVisibility(View.GONE);
                        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
                        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        holder.itemView.setLayoutParams(lp);

//                        mAttacher.update();
//
//                        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
//                            @Override
//                            public void onViewTap(View view, float x, float y) {
//                                if (mAttacher.getScale() <= 1 && !isScroll) {
//                                    if (!isScroll) {
//                                        Log.d(TAG, "onViewTap :" + x + " " + y);
//                                        areaClickHelper.onClick(x, y);
//                                    }
//                                }
//                            }
//                        });

                        Log.d(TAG,"OK:"+url);
                        return false;
                    }
                })
                .into(holder.ivPicture);
    }
    //再次请求，如果有备用图片host修改url
    public void loadImageAgain(PictureItemHolder holder,String url){
        if (ruleStore.getReadRule().get("imghost")!=null && ruleStore.getImgHost()!=null) {
            url = url.replaceAll(ruleStore.getReadRule().get("imghost"), ruleStore.getImgHost());
            loadImage(holder, url, false);
        }else{
            holder.progressBar.setVisibility(View.GONE);
            holder.btnRefresh.setVisibility(View.VISIBLE);
        }
    }

    public AreaClickHelper getAreaClickHelper() {
        return areaClickHelper;
    }

    public void setAreaClickListener(AreaClickHelper.OnAreaClickListener onAreaClickListener) {
        areaClickHelper.setAreaClickListener(onAreaClickListener);
    }
}
