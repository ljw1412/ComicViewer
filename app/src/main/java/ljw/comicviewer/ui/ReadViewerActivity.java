package ljw.comicviewer.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.others.MyViewPager;
import ljw.comicviewer.ui.adapter.PicturePagerAdapter;
import ljw.comicviewer.ui.listeners.OnItemLongClickListener;
import ljw.comicviewer.util.AreaClickHelper;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ReadViewerActivity extends Activity {
    private String TAG = getClass().getSimpleName()+"----";
    private Context context;
    private PicturePagerAdapter picturePagerAdapter;
    private String comic_id,chapter_id,chapter_name;
    private List<String> imgUrls;
    private MyOnItemLongClickListener onItemLongClickListener;
    private int currPos = 0;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.view_pager)
    MyViewPager viewPager;
    @BindView(R.id.rv_picture)
    RecyclerView rvPicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_viewer);
        context = this;
        //全屏化
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ButterKnife.bind(this);
        comic_id = (String) getIntent().getExtras().get("comic_id");
        chapter_id = (String) getIntent().getExtras().get("chapter_id");
        chapter_name = (String) getIntent().getExtras().get("chapter_name");
        currPos = (int) getIntent().getExtras().get("position");
        String[] urls= (String[]) getIntent().getExtras().get("urls");
        imgUrls = strArrayToList(urls);

        Fresco.initialize(getApplicationContext());



        onItemLongClickListener = new MyOnItemLongClickListener();
        initView();
    }

    public List<String> strArrayToList(String[] array){
        List<String> list = new ArrayList<>();
        for (int i = 0;i<array.length;i++){
            list.add(array[i]);
        }
        return list;
    }


    private void initView() {
        viewPager.setVisibility(View.VISIBLE);
        rvPicture.setVisibility(View.GONE);
        picturePagerAdapter = new PicturePagerAdapter(this, imgUrls);
        picturePagerAdapter.setOnItemLongClickListener(onItemLongClickListener);
        picturePagerAdapter.setAreaClickListener(new AreaClickHelper.OnLeftRightClickListener() {
            @Override
            public void left() {
                prevPage();
                Log.d(TAG,"left");
            }

            @Override
            public void right() {
                nextPage();
                Log.d(TAG,"right");
            }

            @Override
            public void center() {
                Log.d(TAG,"center");
            }
        });
        viewPager.setAdapter(picturePagerAdapter);
        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currPos = position;
                //tvCount.setText((position + 1) + "/" + picturePagerAdapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        viewPager.addOnPageChangeListener(listener);
        viewPager.setOffscreenPageLimit(2);//TODO:之后改为可以设置的
        viewPager.setCurrentItem(0);//TODO:跳页


    }

    public void loadImage(final String url, final Object viewHolder, final AreaClickHelper areaClickHelper){
        final PhotoView pic = ((PicturePagerAdapter.PictureViewHolder) viewHolder).ivPicture;
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(pic);

        RequestOptions options = new RequestOptions();

        Log.d(TAG,url+"加载中");
        Glide.with(context)
                .load(new GlideUrl(url
                        ,new LazyHeaders.Builder().addHeader("Referer","http://www.manhuagui.com").build()
                )).into(new DrawableImageViewTarget(pic){
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
                PicturePagerAdapter.PictureViewHolder pictureViewHolder = (PicturePagerAdapter.PictureViewHolder) viewHolder;
                pictureViewHolder.progressBar.setVisibility(View.VISIBLE);
                pictureViewHolder.btnRefresh.setVisibility(View.GONE);
            }

            @Override
            public void onResourceReady(Drawable resource, @Nullable Transition<? super Drawable> transition) {
                super.onResourceReady(resource, transition);
                PicturePagerAdapter.PictureViewHolder pictureViewHolder = (PicturePagerAdapter.PictureViewHolder) viewHolder;
                pictureViewHolder.progressBar.setVisibility(View.GONE);
                pictureViewHolder.btnRefresh.setVisibility(View.GONE);
                mAttacher.update();

                mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        if (mAttacher.getScale() <= 1) {
                            Log.d(TAG,x+" "+y);
                            areaClickHelper.onClick(x, y);
                        }
                    }
                });

//                pictureViewHolder.ivPicture.update(resource.getIntrinsicWidth(),resource.getIntrinsicHeight());
                Log.d(TAG,"OK");
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                Log.e(TAG, "onLoadFailed: "+url+"加载失败！");
                PicturePagerAdapter.PictureViewHolder pictureViewHolder = (PicturePagerAdapter.PictureViewHolder) viewHolder;
                pictureViewHolder.progressBar.setVisibility(View.GONE);
                pictureViewHolder.btnRefresh.setVisibility(View.VISIBLE);
            }
        });
    }

    private void prevPage(){
        int currItem = viewPager.getCurrentItem();
        if (currItem > 0)
            viewPager.setCurrentItem(currItem - 1);
    }

    private void nextPage(){
        int currItem = viewPager.getCurrentItem();
        if (currItem + 1 < viewPager.getAdapter().getCount())
            viewPager.setCurrentItem(currItem + 1);
    }

    private class MyOnItemLongClickListener implements OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(View view, int position) {
            return false;
        }
    }
}
