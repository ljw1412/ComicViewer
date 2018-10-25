package ljw.comicviewer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;

public class RuleItemHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.rule_icon)
    ImageView ruleIcon;
    @BindView(R.id.rule_name)
    TextView ruleName;
    @BindView(R.id.rule_version)
    TextView ruleVersion;
    @BindView(R.id.rule_description)
    TextView ruleDescription;

    public RuleItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
