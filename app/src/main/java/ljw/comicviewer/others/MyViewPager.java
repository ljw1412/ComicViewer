package ljw.comicviewer.others;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import ljw.comicviewer.util.AreaClickHelper;

public class MyViewPager extends ViewPager {
    private boolean mIsDisallowIntercept = false;
    private AreaClickHelper areaClickHelper;

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
}
