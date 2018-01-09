package ljw.comicviewer.util;

import android.content.Context;

import ljw.comicviewer.R;
import ljw.comicviewer.rule.RuleParser;
import ljw.comicviewer.store.AppStatusStore;
import ljw.comicviewer.store.RuleStore;

/**
 * Created by ljw on 2017-12-03 003.
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
}
