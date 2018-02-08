package ljw.comicviewer.util;

import android.content.Context;

import ljw.comicviewer.R;
import ljw.comicviewer.rule.RuleParser;
import ljw.comicviewer.store.AppStatusStore;
import ljw.comicviewer.store.RuleStore;

/**
 * 数据存储工具类
 */

public class StoreUtil {
    public static void initRuleStore(Context context){
        String rule = FileUtil.readJson(context, R.raw.manhuagui);
        RuleStore.get().setCurrentRule(rule.equals("fail") ? null : rule);
        if(RuleStore.get().getCurrentRule()!=null){
            RuleParser.get().parseAll();
        }else{
            throw new RuntimeException("初始化失败");
        }
        RuleStore.get().printRules();
    }

    public static void initRuleStore(Context context,int resId){
        String rule = FileUtil.readJson(context,resId);
        RuleStore.get().setCurrentRule(rule.equals("fail") ? null : rule);
        if(RuleStore.get().getCurrentRule()!=null){
            RuleStore.get().clearAll();
            RuleParser.get().parseAll();
        }else{
            throw new RuntimeException("初始化失败");
        }
        if(AppStatusStore.get().getCurrentSource()==null){
            AppStatusStore.get().setCurrentSource(RuleStore.get().getComeFrom());
        }
        RuleStore.get().printRules();
    }

    public static void switchSource(Context context,int i){
        switch (i){
            case 0:
                StoreUtil.initRuleStore(context,R.raw.manhuagui);
                break;
            case 1:
                StoreUtil.initRuleStore(context,R.raw.manhuatai);
                break;
            case 2:
                StoreUtil.initRuleStore(context,R.raw.zymk);
                break;
            case 3:
                StoreUtil.initRuleStore(context,R.raw.bbhou);
                break;
        }
    }
}
