package ljw.comicviewer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by ljw on 2018-02-05 005.
 */

public class PictureItemHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.iv_picture)
    public PhotoView ivPicture;
    @BindView(R.id.progress_bar)
    public ProgressBar progressBar;
    @BindView(R.id.btn_refresh)
    public ImageView btnRefresh;
    @BindView(R.id.page_num)
    public TextView txtPageNum;
    View view;

    public PictureItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.view = itemView;
    }
}
