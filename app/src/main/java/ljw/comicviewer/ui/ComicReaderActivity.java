package ljw.comicviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Chapter;
import ljw.comicviewer.bean.ManhuaguiComicInfo;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.others.MyViewPager;
import ljw.comicviewer.others.MyWebView;
import ljw.comicviewer.store.ComicReadStore;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.adapter.PicturePagerAdapter;
import ljw.comicviewer.ui.listeners.OnItemLongClickListener;
import ljw.comicviewer.util.AnimationUtil;
import ljw.comicviewer.util.AreaClickHelper;
import ljw.comicviewer.util.SnackbarUtil;
import ljw.comicviewer.util.WebViewUtil;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ComicReaderActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName()+"----";
    private Context context;
    private MyOnItemLongClickListener onItemLongClickListener;
    private PicturePagerAdapter picturePagerAdapter;
    private String comic_id,comic_name,chapter_id,chapter_name;
    private int tryTime = 0, currPos;
    private List<String> imgUrls = new ArrayList<>();
    private boolean isShowTools = true, isScroll = false;
    Intent intent = new Intent();
    private WebView webView;
    private RuleStore ruleStore;
    @BindView(R.id.reader_loading_view)
    View view_loading;
    @BindView(R.id.load_fail)
    LinearLayout layout_load_fail;
    @BindView(R.id.loading)
    LinearLayout layout_loading;
    @BindView(R.id.btn_refresh)
    ImageView refresh;
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
    @BindView(R.id.reader_coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.error_msg)
    TextView txt_err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_reader);
        ButterKnife.bind(this);
        context = this;
        comic_id = (String) getIntent().getExtras().get("comic_id");
        comic_name = (String) getIntent().getExtras().get("comic_name");
        chapter_id = (String) getIntent().getExtras().get("chapter_id");
        chapter_name = (String) getIntent().getExtras().get("chapter_name");
        currPos = (int) getIntent().getExtras().get("position")-1;
        Log.d(TAG, "onCreate: "+currPos);
        //store数据打印
        ComicReadStore.get().printList();
        ruleStore = RuleStore.get();
        view_loading.setOnClickListener(null);
        initWebView();
        setTime();
        addListener();
    }

    private void initWebView(){
        //破解屏蔽
        if (ruleStore.getCookie()!=null) {
            //设置cookie
            WebViewUtil.syncCookie(context, RuleStore.get().getDomain(), ruleStore.getCookie());
        }
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
        webView.loadUrl(ruleStore.getHost()+ruleStore.getDetailsRule().get("url")+comic_id+"/"+chapter_id+"/");
        webView.setWebViewClient(new MyWebView());
        getInfo();
    }

    //getInfo() 获取到数据后 执行这个
    private void initView(){
        txtComicName.setText(comic_name);
        txtComicChapterName.setText(chapter_name);
        txtChapterName.setText(chapter_name);
        setPageText((currPos+1)+"",""+imgUrls.size());
        viewPager.setVisibility(View.VISIBLE);
        rvPicture.setVisibility(View.GONE);
        picturePagerAdapter = new PicturePagerAdapter(this, imgUrls);
        onItemLongClickListener = new MyOnItemLongClickListener();
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
        viewPager.setOffscreenPageLimit(2);//TODO:之后改为可以设置的
        viewPager.setCurrentItem(currPos);//跳页
        initSeekBar();
    }

    public void initSeekBar() {
        mySeekBar.setMax(imgUrls.size() - 1);
        mySeekBar.setProgress(currPos);
        mySeekBar.setSecondaryProgress(currPos);
        txtSeekBarTips.setText((currPos+1) + "/" + imgUrls.size());
    }

    private void addListener(){
        viewMask.setOnClickListener(null);
        viewNotClick.setOnClickListener(null);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_load_fail.setVisibility(View.GONE);
                layout_loading.setVisibility(View.VISIBLE);
                tryTime = 0;
                getInfo();
            }
        });
        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int i, boolean b) {
                txtSeekBarTips.setText((i+1)+"/"+imgUrls.size());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                txtSeekBarTips.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtSeekBarTips.setVisibility(View.GONE);
                gotoPage(seekBar.getProgress());

            }
        });

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
                updateSeekBar(currPos);
                setPageText((position+1)+"",picturePagerAdapter.getCount()+"");
                viewMask.setVisibility(View.GONE);
                intent.putExtra("page",currPos+1);
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
    }

    //获得漫画章节信息
    public void getInfo(){
        setHistory();//TODO:准备规则化
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String js = RuleStore.get().getReadRule().get("wv-js");
                if(js!=null) {
                    webView.evaluateJavascript("cInfo;", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d(TAG, "debug!!=" + s);
                            if (s.equals("null")) {
                                //加载失败
                                if (tryTime >= 3) {
                                    layout_load_fail.setVisibility(View.VISIBLE);
                                    layout_loading.setVisibility(View.GONE);
                                    txt_err.setText(R.string.data_load_fail);
                                } else {
                                    tryTime++;
                                    getInfo();
                                }
                            } else {
                                ManhuaguiComicInfo info = ComicFetcher.parseCurrentChapter(s);
                                for (int i = 0; i < info.getFiles().size(); i++) {
                                    imgUrls.add(RuleStore.get().getImgHost() + info.getPath() + info.getFiles().get(i));
                                }
                                webView.loadUrl("about:blank");
                                initView();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        view_loading.setVisibility(View.GONE);
                                    }
                                }, 500);
                                return;
                            }
                        }
                    });
                }else {
                    layout_load_fail.setVisibility(View.VISIBLE);
                    layout_loading.setVisibility(View.GONE);
                    txt_err.setText(R.string.rule_error);
                }
            }
        }, 1500);
    }

    public void loadImage(final String url, final Object viewHolder, int position){
        final PhotoView pic = ((PicturePagerAdapter.PictureViewHolder) viewHolder).ivPicture;
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(pic);
        //记录下标与PhotoViewAttacher的关系用于回收资源
        picturePagerAdapter.getPVAMap().put(position,mAttacher);
        final AreaClickHelper areaClickHelper = viewPager.getAreaClickHelper();

        RequestOptions options = new RequestOptions();
        Glide.with(context)
                .asBitmap()
                .load(new GlideUrl(url
                        ,new LazyHeaders.Builder().addHeader("Referer",ruleStore.getHost()).build()
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
                pictureViewHolder.txtPageNum.setVisibility(View.GONE);
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

    private void gotoPage(int page){
        currPos = page + 1;
        viewPager.setCurrentItem(page);
    }

    private void prevPage(){
        int currItem = viewPager.getCurrentItem();
        if (currItem > 0) {
            currPos = currItem - 1;
            viewPager.setCurrentItem(currItem - 1);
            updateSeekBar(currPos);
        }
        else gotoLoading(false);
    }

    private void nextPage(){
        int currItem = viewPager.getCurrentItem();
        if (currItem + 1 < viewPager.getAdapter().getCount()){
            currPos = currItem;
            viewPager.setCurrentItem(currItem + 1);
            updateSeekBar(currPos);
        }
        else gotoLoading(true);
    }

    private void gotoLoading(boolean isAdd){
        ComicReadStore comicReadStore = ComicReadStore.get();
        int index = comicReadStore.getCurrentIndex();
        //如果是第一或最后的提示
        if (index == 0 && !isAdd){
            SnackbarUtil.newAddImageColorfulSnackar(
                    coordinatorLayout,
                    getString(R.string.tips_is_first),
                    R.drawable.icon_warning,
                    ContextCompat.getColor(context, R.color.circular_blue),
                    ContextCompat.getColor(context, R.color.white)).show();
        }else if (index == comicReadStore.getSize()-1 && isAdd){
            SnackbarUtil.newAddImageColorfulSnackar(
                    coordinatorLayout,
                    getString(R.string.tips_is_last),
                    R.drawable.icon_warning,
                    ContextCompat.getColor(context, R.color.circular_blue),
                    ContextCompat.getColor(context, R.color.white)).show();
        }else{
            if (isAdd){
                index++;
            }else{
                index--;
            }
            Chapter toChapter = comicReadStore.getObj().get(index);
            comicReadStore.setCurrentIndex(index);
            comic_id = toChapter.getComicId();
            chapter_id = toChapter.getChapterId();
            chapter_name = toChapter.getChapterName();
            loadChapter();
        }
    }

    private void loadChapter(){
        view_loading.setVisibility(View.VISIBLE);
        webView.loadUrl(RuleStore.get().getHost()+ruleStore.getDetailsRule().get("url")+comic_id+"/"+chapter_id+"/");
        imgUrls.clear();
        currPos = 0;
        getInfo();
    }

    private void setHistory(){
        intent.putExtra("chapterId",chapter_id);
        intent.putExtra("chapterName",chapter_name);
        setResult(Global.REQUEST_COMIC_HISTORY,intent);
    }

    private void updateSeekBar(int progress){
        mySeekBar.setProgress(progress);
        Log.d(TAG, "updateSeekBar: "+progress);
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
        if (webView!=null){
            webView.destroy();
        }
    }
}
