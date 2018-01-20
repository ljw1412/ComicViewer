package ljw.comicviewer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Theme;

/**
 * Created by ljw on 2018-01-19 019.
 */

public class ThemeGridAdapter extends BaseAdapter {
    List<Theme> themes;
    private Context context;

    public ThemeGridAdapter(Context context ,List<Theme> themes) {
        this.context = context;
        this.themes = themes;
    }

    @Override
    public int getCount() {
        return themes.size();
    }

    @Override
    public Object getItem(int i) {
        return themes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ThemeItemHolder themeItemHolder;
        if(view==null || view.getTag()==null){
            view = LayoutInflater.from(context).inflate(R.layout.item_theme,null);
            themeItemHolder = new ThemeItemHolder(view);
            view.setTag(themeItemHolder);
        }else{
            themeItemHolder = (ThemeItemHolder) view.getTag();
        }
        themeItemHolder.color.setBackgroundColor(themes.get(position).getColor());
        themeItemHolder.colorName.setText(themes.get(position).getName());
        themeItemHolder.colorName.setTextColor(themes.get(position).getColor());
        themeItemHolder.checked.setVisibility(themes.get(position).isChecked()?View.VISIBLE:View.GONE);
        return view;
    }


    class ThemeItemHolder{
        @BindView(R.id.theme_item_color)
        ImageView color;
        @BindView(R.id.theme_item_color_name)
        TextView colorName;
        @BindView(R.id.theme_item_checked)
        ImageView checked;
        View view;

        public ThemeItemHolder(View view) {
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }
}

