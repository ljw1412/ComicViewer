package ljw.comicviewer.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.ui.ReadViewerActivity;
import ljw.comicviewer.ui.listeners.OnItemLongClickListener;
import ljw.comicviewer.util.AreaClickHelper;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by ljw on 2017-09-10 010.
 */

public class PicturePagerAdapter extends PagerAdapter {
    private ReadViewerActivity context;
    private List<String> imgUrls;
    private OnItemLongClickListener mOnItemLongClickListener;
    private List<PictureViewHolder> viewHolders = new ArrayList<>();
    private AreaClickHelper areaClickHelper;

    public PicturePagerAdapter(ReadViewerActivity context, List<String> imgUrls) {
        this.context = context;
        this.imgUrls = imgUrls;
        for (int i = 0; i < imgUrls.size(); i++){
            viewHolders.add(null);
        }
        areaClickHelper = new AreaClickHelper(context);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setAreaClickListener(AreaClickHelper.OnAreaClickListener onAreaClickListener) {
        areaClickHelper.setAreaClickListener(onAreaClickListener);
    }

    public PictureViewHolder getViewHolderAt(int position) {
        if (position >= 0 && position < viewHolders.size())
            return viewHolders.get(position);
        else
            return null;
    }

    @Override
    public int getCount() {
        return (imgUrls == null || imgUrls.size() == 0 ? 1 : imgUrls.size());
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (viewHolders.size() > position && viewHolders.get(position) != null) {
            if (viewHolders.get(position).view != null)
                container.removeView(viewHolders.get(position).view);
            viewHolders.set(position, null);
        }
    }


    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_read_viewer, null);
        final PictureViewHolder viewHolder = new PictureViewHolder(view);
        if (imgUrls != null && position < imgUrls.size()) {
            final String url = imgUrls.get(position);
            context.loadImage(url,viewHolder,areaClickHelper);
            viewHolder.btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {context.loadImage(url,viewHolder,areaClickHelper);
                }
            });
            viewHolder.ivPicture.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                if (mOnItemLongClickListener != null)
                    return mOnItemLongClickListener.onItemLongClick(view, position);
                else
                    return false;
                }
            });
        }
        viewHolders.set(position, viewHolder);
        container.addView(viewHolder.view, 0);
        return viewHolder.view;
    }


    public class PictureViewHolder {
        @BindView(R.id.iv_picture)
        public PhotoView ivPicture;
// !!!       public PhotoDraweeView ivPicture;
        @BindView(R.id.progress_bar)
        public ProgressBarCircularIndeterminate progressBar;
        @BindView(R.id.btn_refresh)
        public ImageView btnRefresh;
        View view;

        public PictureViewHolder(View view) {
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

}
