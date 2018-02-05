package ljw.comicviewer.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import ljw.comicviewer.R;
import ljw.comicviewer.store.RuleStore;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by ljw on 2018-02-05 005.
 */

public class PictureRecyclerViewAdapter extends RecyclerView.Adapter<PictureItemHolder>{
    private String TAG = getClass().getSimpleName()+"----";
    private Context context;
    private List<String> imgUrls;
    private RuleStore ruleStore = RuleStore.get();

    public PictureRecyclerViewAdapter(Context context, List<String> imgUrls) {
        this.context = context;
        this.imgUrls = imgUrls;
    }

    @Override
    public void onViewRecycled(PictureItemHolder holder) {
        if (holder!=null){
            Glide.with(context).clear(holder.ivPicture);
        }
        super.onViewRecycled(holder);
    }


    @Override
    public PictureItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_read_viewer,parent,false);
        return new PictureItemHolder(v);
    }

    @Override
    public void onBindViewHolder(PictureItemHolder holder, int position) {
        if (getItemCount()<=0) return;
        holder.txtPageNum.setText((position+1)+"");
        String url = imgUrls.get(position);
        loadImage(holder,url,true);
    }

    @Override
    public int getItemCount() {
        return imgUrls != null ? imgUrls.size() : 0;
    }

    public void loadImage(final PictureItemHolder holder, final String url, final boolean first){
        final PhotoView pic = holder.ivPicture;
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(pic);

//        final AreaClickHelper areaClickHelper = viewPager.getAreaClickHelper();

        RequestOptions options = new RequestOptions();
        if(!((Activity) context).isDestroyed())
        Glide.with(context)
                .asBitmap()
                .load(new GlideUrl(url
                        ,new LazyHeaders.Builder().addHeader("Referer",ruleStore.getHost()).build()
                ))
                .into(new BitmapImageViewTarget(pic){
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        Log.d(TAG,url+" 开始加载");
                        holder.txtPageNum.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.btnRefresh.setVisibility(View.GONE);
                    }


                    @Override
                    public void onResourceReady(Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        super.onResourceReady(bitmap, transition);
                        holder.progressBar.setVisibility(View.GONE);
                        holder.btnRefresh.setVisibility(View.GONE);
                        holder.txtPageNum.setVisibility(View.GONE);
                        mAttacher.update();

                        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                            @Override
                            public void onViewTap(View view, float x, float y) {
//                        if (mAttacher.getScale() <= 1 && !isScroll) {
//                                if (!isScroll) {
//                                    Log.d(TAG,"onViewTap :"+x+" "+y);
//                                    areaClickHelper.onClick(x, y);
//                                }
                            }
                        });

                        Log.d(TAG,"OK");
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Log.e(TAG, "onLoadFailed: "+url+"加载失败！");
                        if (!first || ruleStore.getReadRule().get("imghost")==null) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.btnRefresh.setVisibility(View.VISIBLE);
                        }else {
                            loadImageAgain(holder,url);
                        }
                    }
                });
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
}
