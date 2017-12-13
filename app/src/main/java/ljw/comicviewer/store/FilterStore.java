package ljw.comicviewer.store;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ljw.comicviewer.bean.Category;

/**
 * Created by ljw on 2017-12-07 007.
 */
//类型筛选状态存储
public class FilterStore {
    private final String TAG = this.getClass().getSimpleName()+"----";
    private static FilterStore filterStore;
    private List<String> order;//父类型的筛选顺序
    private Map<String,String> filterStatus;//当前筛选状态//TODO:改为Map<String,String>,<父类型，子类型>
    private String endStr;//结尾添加
    private String separate;//分隔符号

    public FilterStore() {
        filterStatus  = new HashMap<>();
    }

    public static FilterStore get(){
        if(filterStore == null){
            filterStore = new FilterStore();
        }
        return filterStore;
    }

    public List<String> getOrder() {
        return order;
    }

    public void setOrder(List<String> order) {
        this.order = order;
    }

    public Map<String,String> getFilterStatus() {
        return filterStatus;
    }

    public void setFilterStatus(String parent,String subType) {
        filterStatus.put(parent, subType);
    }

    public String getEndStr() {
        return endStr;
    }

    public void setEndStr(String endStr) {
        this.endStr = endStr;
    }

    public String getSeparate() {
        return separate;
    }

    public void setSeparate(String separate) {
        this.separate = separate;
    }

    public void filterStatusReset(){
        Map<String,List<Category>> map = RuleStore.get().getTypeRule();
        if(map!=null && map.size()>0){
            for(String key:order) {
                for (Category category:map.get(key)){
                    if(category!=null && category.getName()!=null
                            && category.getName().equals("全部")){
                        setFilterStatus(key,category.getValue());
                        break;
                    }
                }
            }
        }
    }

    public void printStore(){
        Log.d(TAG, "FilterStore情况:" +
                "order=" + order +
                ", filterStatus=" + filterStatus +
                ", endStr='" + endStr + '\'' +
                ", separate='" + separate);
    }

}
