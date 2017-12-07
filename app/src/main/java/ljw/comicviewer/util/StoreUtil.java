package ljw.comicviewer.util;

import android.content.Context;

import ljw.comicviewer.rule.RuleParser;
import ljw.comicviewer.store.RuleStore;

/**
 * Created by ljw on 2017-12-03 003.
 */

public class StoreUtil {
    public static void initRuleStore(Context context){
        String rule = FileUtil.readJson(context);
        RuleStore.get().setCurrentRule(rule.equals("fail") ? null : rule);
        if(RuleStore.get().getCurrentRule()!=null){
            RuleParser.get().parseAll();
        }else{
            throw new RuntimeException("初始化失败");
        }
        RuleStore.get().printRules();
    }
}
