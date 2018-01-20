package ljw.comicviewer.util;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 动画工具类
 */

public class AnimationUtil {
    private static final String TAG = AnimationUtil.class.getSimpleName();

    /**
     * 从控件所在位置移动到控件的底部
     * @return
     */
    public static TranslateAnimation moveToViewBottomOut() {
        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        anim.setDuration(300);
        return anim;
    }

    /**
     * 从控件的底部移动到控件所在位置
     * @return
     */
    public static TranslateAnimation moveToViewBottomIn() {
        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        anim.setDuration(300);
        return anim;
    }

    /**
     * 从控件所在位置移动到控件的顶部
     * @return
     */
    public static TranslateAnimation moveToViewTopOut() {
        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        anim.setDuration(300);
        return anim;
    }

    /**
     * 从控件的顶部移动到控件所在位置
     * @return
     */
    public static TranslateAnimation moveToViewTopIn() {
        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        anim.setDuration(300);
        return anim;
    }

    /**
     * 渐入
     * @return
     */
    public static AlphaAnimation fadeIn(int duration){
        AlphaAnimation anim = new AlphaAnimation(0.0f,1.0f);
        anim.setDuration(duration);
        return anim;
    }

    /**
     * 渐出
     * @return
     */
    public static AlphaAnimation fadeOut(int duration){
        AlphaAnimation anim = new AlphaAnimation(1.0f,0.0f);
        anim.setDuration(duration);
        return anim;
    }

    /**
     * 从小变大(中心)
     * @return
     */
    public static ScaleAnimation smallToLarge(int duration){
        ScaleAnimation anim = new ScaleAnimation(0,1.0f,0,1.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        anim.setDuration(duration);
        return anim;
    }

    /**
     * 从小变大(中心)
     * @return
     */
    public static ScaleAnimation largeToSmall(int duration){
        ScaleAnimation anim = new ScaleAnimation(1.0f,0,1.0f,0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        anim.setDuration(duration);
        return anim;
    }

    //水平移动
    public static TranslateAnimation moveHorizontal(float px) {
        TranslateAnimation anim = new TranslateAnimation(0,px,0,0);
        anim.setDuration(300);
        return anim;
    }
}
