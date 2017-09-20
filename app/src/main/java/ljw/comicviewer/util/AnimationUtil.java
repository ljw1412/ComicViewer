package ljw.comicviewer.util;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by ljw on 2017-09-17 017.
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
    public static AlphaAnimation fadeIn(){
        AlphaAnimation anim = new AlphaAnimation(0.0f,1.0f);
        anim.setDuration(300);
        return anim;
    }

    /**
     * 渐出
     * @return
     */
    public static AlphaAnimation fadeOut(){
        AlphaAnimation anim = new AlphaAnimation(1.0f,0.0f);
        anim.setDuration(300);
        return anim;
    }
}
