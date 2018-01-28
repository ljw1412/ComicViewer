package ljw.comicviewer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;

/**
 * Created by ljw on 2018-01-28 028.
 */

public class FilterItemViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.comic_score)
    TextView score;
    @BindView(R.id.comic_updateDate)
    TextView update;
    @BindView(R.id.comic_updateStatus)
    TextView updateStatus;
    @BindView(R.id.comic_name)
    TextView name;
    @BindView(R.id.comic_img)
    ImageView image;
    @BindView(R.id.comic_status)
    ImageView isEnd;

    public FilterItemViewHolder(View view) {
        super(view);
        ButterKnife.bind(this,view);
    }
}
