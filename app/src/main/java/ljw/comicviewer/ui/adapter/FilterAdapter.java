package ljw.comicviewer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Category;

/**
 * 分类条目适配器
 */

public class FilterAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Category> categories;
    //用map防止滚动样式重构
    public HashMap<Integer, View> viewMap = new HashMap<>();
    public FilterAdapter(Context context, List<Category> categories) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        FilterViewHolder filterViewHolder;
        if (!viewMap.containsKey(position) || viewMap.get(position) == null) {
            view = inflater.inflate(R.layout.item_filter,null);
            filterViewHolder = new FilterViewHolder(view);
            view.setTag(filterViewHolder);
            viewMap.put(position, view);
        }else {
            view = viewMap.get(position);
            filterViewHolder = (FilterViewHolder) view.getTag();
        }
        filterViewHolder.text.setText(categories.get(position).getName());
        filterViewHolder.text.setSelected(categories.get(position).isSelected());
        return view;
    }




    public class FilterViewHolder{
        @BindView(R.id.item_category_txt)
        TextView text;
        View view;

        public FilterViewHolder(View view) {
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }
}
