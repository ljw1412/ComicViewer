package ljw.comicviewer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;

/**
 * Created by ljw on 2018-02-10 010.
 */

public class SearchItemHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.search_body)
    LinearLayout body;
    @BindView(R.id.search_cover)
    ImageView image;
    @BindView(R.id.search_comic_name)
    TextView name;
    @BindView(R.id.search_comic_author)
    TextView author;
    @BindView(R.id.search_comic_update)
    TextView update;
    @BindView(R.id.search_comic_update_status)
    TextView updateStatus;
    @BindView(R.id.search_comic_type)
    TextView type;
    @BindView(R.id.search_comic_info)
    TextView info;
    @BindView(R.id.search_comic_is_end)
    TextView end;
    public SearchItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
