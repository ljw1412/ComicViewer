package ljw.comicviewer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;

/**
 * Created by ljw on 2018-01-27 027.
 */

public class HistoryItemViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.history_comic_name)
    TextView comicName;
    @BindView(R.id.history_cover)
    ImageView image;
    @BindView(R.id.history_comic_chapterName)
    TextView chapterName;
    @BindView(R.id.history_comic_page)
    TextView page;
    @BindView(R.id.history_comic_readTime)
    TextView readTime;
    @BindView(R.id.history_comic_is_end)
    TextView end;
    View view;

    public HistoryItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        view = itemView;
    }
}