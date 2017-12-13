package ljw.comicviewer.ui.adapter;

import android.content.Context;
import android.graphics.Color;
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
import ljw.comicviewer.util.DisplayUtil;

/**
 * Created by ljw on 2017-12-11 011.
 */

public class CategoryGridAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Category> categories;
    //用map防止滚动样式重构
    public HashMap<Integer, View> viewMap = new HashMap<>();
    public CategoryGridAdapter(Context context, List<Category> categories) {
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
        CategoryViewHolder categoryViewHolder;
        if (!viewMap.containsKey(position) || viewMap.get(position) == null) {
            view = inflater.inflate(R.layout.item_category,null);
            categoryViewHolder = new CategoryViewHolder(view);
            //添加时固定颜色
            categoryViewHolder.view.setBackgroundColor(randomColor());
            view.setTag(categoryViewHolder);
            viewMap.put(position, view);
        }else {
            view = viewMap.get(position);
            categoryViewHolder = (CategoryViewHolder) view.getTag();
        }
        categoryViewHolder.text.setText(categories.get(position).getName());
        return view;
    }

    private int randomColor(){
        return Color.argb(150,DisplayUtil.randomInt(50, 220), DisplayUtil.randomInt(50, 220), DisplayUtil.randomInt(50, 220));
    }


    public class CategoryViewHolder{
        @BindView(R.id.item_category_txt)
        TextView text;
        View view;

        public CategoryViewHolder(View view) {
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }
}
