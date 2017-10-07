package ljw.comicviewer.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Chapter;
import ljw.comicviewer.others.MyViewPager;
import ljw.comicviewer.store.ComicReadStore;
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
    private boolean isScroll = false;
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
    RelativeLayout viewBottomTools;
    @BindView(R.id.read_viewer_can_not_click)
    RelativeLayout viewNotClick;
    @BindView(R.id.read_viewer_status)
    LinearLayout viewBottomStatus;
    @BindView(R.id.read_viewer_comic_name)
    TextView txtComicName;
    @BindView(R.id.read_viewer_comic_chapter_name)
    TextView txtComicChapterName;
    @BindView(R.id.read_viewer_tools_page)
    TextView txtToolsPage;
    @BindView(R.id.read_viewer_chapter_name)
    TextView txtChapterName;
    @BindView(R.id.read_viewer_mask)
    RelativeLayout viewMask;
    @BindView(R.id.read_viewer_seekBar)
    SeekBar mySeekBar;
    @BindView(R.id.read_viewer_seekbar_tips)
    TextView txtSeekBarTips;

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
        currPos = (int) getIntent().getExtras().get("position") - 1;
        String[] urls= (String[]) getIntent().getExtras().get("urls");
        imgUrls = DisplayUtil.strArrayToList(urls);


        //store数据打印
        ComicReadStore.get().printList();

        viewMask.setOnClickListener(null);
        onItemLongClickListener = new MyOnItemLongClickListener();
        initView();
        initSeekBar();
        setTime();
    }

    private void initView() {
        viewNotClick.setOnClickListener(null);
        txtComicName.setText(comic_name);
        txtComicChapterName.setText(chapter_name);
        txtChapterName.setText(chapter_name);
        setPageText((currPos+1)+"",""+imgUrls.size());
        viewPager.setVisibility(View.VISIBLE);
        rvPicture.setVisibility(View.GONE);

        picturePagerAdapter = new PicturePagerAdapter(this, imgUrls);
        picturePagerAdapter.setOnItemLongClickListener(onItemLongClickListener);

        viewPager.setAreaClickListener(new AreaClickHelper.OnLeftRightClickListener() {
            @Override
            public void left() {
                if(isShowTools){
                    showOrHideTools();
                }
                if(!isScroll){
                    prevPage();
                    Log.d(TAG,"left");
                }
            }

            @Override
            public void right() {
                if(isShowTools){
                    showOrHideTools();
                }
                if(!isScroll){
                    nextPage();
                    Log.d(TAG,"right");
                }
            }

            @Override
            public void center() {
                if(!isScroll) {
                    showOrHideTools();
                    Log.d(TAG,"center");
                }
            }
        });
        viewPager.setAdapter(picturePagerAdapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            //TIPS:PhotoViewAttacher加载时会拦截此监听
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float DownX = 0,DownY = 0,MoveX = 0,MoveY;
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN://0
                        DownX = motionEvent.getX();
                        DownY = motionEvent.getY();
//                        Log.d(TAG, "onTouch: "+motionEvent.getAction()+" "+DownX+","+DownY);
                        break;
                    case MotionEvent.ACTION_MOVE://2
                        MoveX = DownX - motionEvent.getX();
                        MoveY = DownY - motionEvent.getY();
//                        Log.d(TAG, "onTouch: "+motionEvent.getAction()+" "+MoveX+","+MoveY);
                        break;
                    case MotionEvent.ACTION_UP://1
                        DownX = motionEvent.getX();
                        DownY = motionEvent.getY();
                        if(Math.abs(MoveX)<20){
                            viewPager.getAreaClickHelper().onClick(DownX,DownY);
                        }
                        break;
                }
                return false;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currPos = position;
                setPageText((position+1)+"",picturePagerAdapter.getCount()+"");
                viewMask.setVisibility(View.GONE);
            }

            private int prePage = -1;
            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        //（1）表示用户手指“按在屏幕上并且开始拖动”的状态（手指按下但是还没有拖动的时候还不是这个状态，只有按下并且手指开始拖动后log才打出。）
                        prePage = currPos;
                        if (isShowTools) {
                            showOrHideTools();
                        }
                        isScroll = true;
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        //（2）在“手指离开屏幕”的状态
                        viewMask.setVisibility(View.VISIBLE);//用遮罩层阻止其他事件并发，减少效果错误
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        //（0）滑动动画做完的状态。
                        viewMask.setVisibility(View.GONE);
                        Log.d(TAG, "onPageScrollStateChanged: prePage=" + prePage + ",currPos:" + currPos);
                        Log.d(TAG, "onPageScrollStateChanged: "+viewPager.getMoveStatus());
                        if(currPos == prePage){
                            if (currPos == 0 && viewPager.getMoveStatus() == MyViewPager.MOVE_LEFT) {
                                gotoLoading(false);
                                Log.d(TAG, "onPageScrollStateChanged: " + "第一页");
                            }
                            if (currPos == picturePagerAdapter.getCount() - 1 && viewPager.getMoveStatus() == MyViewPager.MOVE_RIGHT) {
                                gotoLoading(true);
                                Log.d(TAG, "onPageScrollStateChanged: " + "最后一页");
                            }
                        }
                        isScroll = false;
                        break;
                }
            }

        });
        viewPager.setOffscreenPageLimit(2);//TODO:之后改为可以设置的
        viewPager.setCurrentItem(0);//TODO:跳页
    }

    public void initSeekBar(){
        mySeekBar.setMax(imgUrls.size()-1);
        mySeekBar.setProgress(currPos);
        mySeekBar.setSecondaryProgress(currPos);
        txtSeekBarTips.setText((currPos+1)+"/"+imgUrls.size());
        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int i, boolean b) {
//                txtSeekBarTips.setText(i);
                try {

                    txtSeekBarTips.setText((i+1)+"/"+imgUrls.size());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void loadImage(final String url, final Object viewHolder,int position){
        final PhotoView pic = ((PicturePagerAdapter.PictureViewHolder) viewHolder).ivPicture;
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(pic);
        //记录下标与PhotoViewAttacher的关系用于回收资源
        picturePagerAdapter.getPVAMap().put(position,mAttacher);
        final AreaClickHelper areaClickHelper = viewPager.getAreaClickHelper();

        RequestOptions options = new RequestOptions();

        Glide.with(context)
            .asBitmap()
            .load(new GlideUrl(url
                    ,new LazyHeaders.Builder().addHeader("Referer","http://www.manhuagui.com").build()
            )).into(new BitmapImageViewTarget(pic){
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
                Log.d(TAG,url+" 开始加载");
                PicturePagerAdapter.PictureViewHolder pictureViewHolder = (PicturePagerAdapter.PictureViewHolder) viewHolder;
                pictureViewHolder.progressBar.setVisibility(View.VISIBLE);
                pictureViewHolder.btnRefresh.setVisibility(View.GONE);
            }


            @Override
            public void onResourceReady(Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                super.onResourceReady(bitmap, transition);
                PicturePagerAdapter.PictureViewHolder pictureViewHolder = (PicturePagerAdapter.PictureViewHolder) viewHolder;
                pictureViewHolder.progressBar.setVisibility(View.GONE);
                pictureViewHolder.btnRefresh.setVisibility(View.GONE);
//                pic.setImageBitmap();
                mAttacher.update();

                mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        if (mAttacher.getScale() <= 1 && !isScroll) {
                            Log.d(TAG,"onViewTap :"+x+" "+y);
                            areaClickHelper.onClick(x, y);
                        }
                    }
                });

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
        else
            gotoLoading(false);
    }

    private void nextPage(){
        int currItem = viewPager.getCurrentItem();
        if (currItem + 1 < viewPager.getAdapter().getCount())
            viewPager.setCurrentItem(currItem + 1);
        else
            gotoLoading(true);
    }

    private void showOrHideTools(){
        if(!isScroll){
            if (!isShowTools){
                Log.d(TAG, "showOrHideTools: 显示");
                viewBottomStatus.setVisibility(View.GONE);
                viewBottomStatus.setAnimation(AnimationUtil.fadeOut(300));
                viewBottomTools.setVisibility(View.VISIBLE);
                viewBottomTools.setAnimation(AnimationUtil.moveToViewBottomIn());
                viewHead.setVisibility(View.VISIBLE);
                viewHead.setAnimation(AnimationUtil.moveToViewTopIn());
            }else {
                Log.d(TAG, "showOrHideTools: 隐藏");
                viewBottomStatus.setVisibility(View.VISIBLE);
                viewBottomStatus.setAnimation(AnimationUtil.fadeIn(300));
                viewBottomTools.setVisibility(View.GONE);
                viewBottomTools.setAnimation(AnimationUtil.moveToViewBottomOut());
                viewHead.setVisibility(View.GONE);
                viewHead.setAnimation(AnimationUtil.moveToViewTopOut());
            }
            isShowTools = !isShowTools;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void gotoLoading(boolean isAdd){
        ComicReadStore comicReadStore = ComicReadStore.get();
        int index = comicReadStore.getCurrentIndex();
        if (index == 0 && !isAdd){
            Toast.makeText(context, R.string.tips_is_first,Toast.LENGTH_LONG).show();
        }else if (index == comicReadStore.getSize()-1 && isAdd){
            Toast.makeText(context,R.string.tips_is_last,Toast.LENGTH_LONG).show();
        }else{
            if (isAdd){
                index++;
            }else{
                index--;
            }
            Chapter preChapter = comicReadStore.getObj().get(index);
            comicReadStore.setCurrentIndex(index);
            gotoReadView(preChapter.getChapter_id(),preChapter.getChapter_name(),
                    isAdd ? Global.RIGHT : Global.LEFT);
        }
    }

    public void gotoReadView(String chapterId,String chapterName,int animDirection){
        Intent intent = new Intent(context,ReadViewerLoadingActivity.class);
        intent.putExtra("comic_id",comic_id);
        intent.putExtra("comic_name",comic_name);
        intent.putExtra("chapter_id",chapterId);
        intent.putExtra("chapter_name",chapterName);
        intent.putExtra("position",1);
        intent.putExtra("anim_mode",animDirection);
        startActivity(intent);

        //设置切换动画
        switch (animDirection){
            case Global.LEFT:
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                break;
            case Global.RIGHT:
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
        }

        finish();
    }
}
