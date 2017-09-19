package ljw.comicviewer.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.others.MyViewPager;
import ljw.comicviewer.ui.adapter.PicturePagerAdapter;
import ljw.comicviewer.ui.listeners.OnItemLongClickListener;
import ljw.comicviewer.util.AnimationUtil;
import ljw.comicviewer.util.AreaClickHelper;
import ljw.comicviewer.util.DisplayUtil;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ReadViewerActivity extends Activity {
    private String TAG = getClass().getSimpleName()+"----";
    private Context context;
    private PicturePagerAdapter picturePagerAdapter;
    private String comic_id,comic_name,chapter_id,chapter_name;
    private List<String> imgUrls;
    private MyOnItemLongClickListener onItemLongClickListener;
    private boolean isShowTools = false;
    private int currPos = 0;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.view_pager)
    MyViewPager viewPager;
    @BindView(R.id.rv_picture)
    RecyclerView rvPicture;
    @BindView(R.id.read_viewer_page)
    TextView txtPage;
    @BindView(R.id.read_viewer_time)
    TextView txtTime;
    @BindView(R.id.read_viewer_head)
    RelativeLayout viewHead;
    @BindView(R.id.read_viewer_tools)
    LinearLayout viewBottomTools;
    @BindView(R.id.read_viewer_status)
    LinearLayout viewBottomStatus;
    @BindView(R.id.read_viewer_comic_name)
    TextView txtComicName;
    @BindView(R.id.read_viewer_comic_chapter_name)
    TextView txtComicChapterName;
    @BindView(R.id.read_viewer_tools_page)
    TextView txtToolsPage;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_viewer);
        context = this;
        ButterKnife.bind(this);

        comic_id = (String) getIntent().getExtras().get("comic_id");
        comic_name = (String) getIntent().getExtras().get("comic_name");
        chapter_id = (String) getIntent().getExtras().get("chapter_id");
        chapter_name = (String) getIntent().getExtras().get("chapter_name");
        currPos = (int) getIntent().getExtras().get("position");
        String[] urls= (String[]) getIntent().getExtras().get("urls");
        imgUrls = DisplayUtil.strArrayToList(urls);

        onItemLongClickListener = new MyOnItemLongClickListener();
        initView();
        setTime();
    }

    private void initView() {
        txtComicName.setText(comic_name);
        txtComicChapterName.setText(chapter_name);
        setPageText(currPos+"",""+imgUrls.size());
        viewPager.setVisibility(View.VISIBLE);
        rvPicture.setVisibility(View.GONE);

        picturePagerAdapter = new PicturePagerAdapter(this, imgUrls);
        picturePagerAdapter.setOnItemLongClickListener(onItemLongClickListener);
        picturePagerAdapter.setAreaClickListener(new AreaClickHelper.OnLeftRightClickListener() {
            @Override
            public void left() {
                if(isShowTools){
                    showOrHideTools();
                }else{
                    prevPage();
                }
                Log.d(TAG,"left");
            }

            @Override
            public void right() {
                if(isShowTools){
                    showOrHideTools();
                }else {
                    nextPage();
                }
                Log.d(TAG,"right");
            }

            @Override
            public void center() {
                showOrHideTools();
                Log.d(TAG,"center");
            }
        });
        viewPager.setAdapter(picturePagerAdapter);
        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(isShowTools){
                    showOrHideTools();
                }
            }

            @Override
            public void onPageSelected(int position) {
                currPos = position;
                setPageText((position+1)+"",picturePagerAdapter.getCount()+"");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        viewPager.addOnPageChangeListener(listener);
        viewPager.setOffscreenPageLimit(3);//TODO:之后改为可以设置的
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

    private void showOrHideTools(){
        if (!isShowTools){
            viewBottomStatus.setVisibility(View.GONE);
            viewBottomStatus.setAnimation(AnimationUtil.fadeOut());
            viewBottomTools.setVisibility(View.VISIBLE);
            viewBottomTools.setAnimation(AnimationUtil.moveToViewBottomIn());
            viewHead.setVisibility(View.VISIBLE);
            viewHead.setAnimation(AnimationUtil.moveToViewTopIn());
        }else {
            viewBottomStatus.setVisibility(View.VISIBLE);
            viewBottomStatus.setAnimation(AnimationUtil.fadeIn());
            viewBottomTools.setVisibility(View.GONE);
            viewBottomTools.setAnimation(AnimationUtil.moveToViewBottomOut());
            viewHead.setVisibility(View.GONE);
            viewHead.setAnimation(AnimationUtil.moveToViewTopOut());
        }
        isShowTools = !isShowTools;
    }

    private class MyOnItemLongClickListener implements OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(View view, int position) {
            return false;
        }
    }


    //1秒刷新一次时间
    public void updateTime(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setTime();
            }
        },1000);
    }

    public void setTime(){
        long sysTime = System.currentTimeMillis();//获取系统时间
        CharSequence sysTimeStr = DateFormat.format("HH:mm", sysTime);//时间显示格式
        txtTime.setText(sysTimeStr);
        updateTime();
    }

    private void setPageText(String current,String total){
        txtPage.setText(current+"/"+total);
        txtToolsPage.setText(current+"/"+total);
    }

    public void onBack(View view){
        finish();
    }
}
