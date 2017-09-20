package ljw.comicviewer.others;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import ljw.comicviewer.util.AreaClickHelper;

public class MyViewPager extends ViewPager {
    private boolean mIsDisallowIntercept = false;
    private AreaClickHelper areaClickHelper;
    private float DownX,moveX;
    public static final int MOVE_LEFT=0;
    public static final int MOVE_RIGHT=1;
    private int moveStatus = -1;

    public MyViewPager(Context context) {
        super(context);
        areaClickHelper = new AreaClickHelper(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        areaClickHelper = new AreaClickHelper(context);
    }

    public void setAreaClickListener(AreaClickHelper.OnAreaClickListener onAreaClickListener) {
        areaClickHelper.setAreaClickListener(onAreaClickListener);
    }

    public AreaClickHelper getAreaClickHelper(){
        return areaClickHelper;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        //判断左右滑动
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                DownX = ev.getX();//float DownX
                moveX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                moveX += ev.getX() - DownX;//X轴距离
                DownX = ev.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(moveX)>20){
                    //水平滑动距离大于20,认为是水平滑动。
//                    Log.d("MyViewPager----", "dispatchTouchEvent: moveX="+moveX);
                    moveStatus = moveX > 0? MOVE_LEFT : MOVE_RIGHT;
                }
                break;
        }

        if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
            requestDisallowInterceptTouchEvent(false);
            boolean handled = super.dispatchTouchEvent(ev);
            requestDisallowInterceptTouchEvent(true);
            return handled;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    public int getMoveStatus() {
        return moveStatus;
    }
}
