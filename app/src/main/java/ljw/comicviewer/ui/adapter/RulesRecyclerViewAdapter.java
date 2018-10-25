package ljw.comicviewer.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.R;
import ljw.comicviewer.bean.RuleGuide;

public class RulesRecyclerViewAdapter extends RecyclerView.Adapter<RuleItemHolder>{
    private Context context;
    private RuleGuide ruleGuide;
    private List<RuleGuide.RuleBlock> ruleList;
    private LayoutInflater inflater;

    public RulesRecyclerViewAdapter(Context context, RuleGuide ruleGuide) {
        this.context = context;
        this.ruleGuide = ruleGuide;
        this.inflater = LayoutInflater.from(context);
        this.ruleList = ruleGuide != null ? ruleGuide.getRuleList() : new ArrayList<>();
    }


    @Override
    public RuleItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_rule,parent,false);
        RuleItemHolder ruleItemHolder = new RuleItemHolder(view);
        return ruleItemHolder;
    }

    @Override
    public void onBindViewHolder(RuleItemHolder holder, int position) {
        RuleGuide.RuleBlock ruleBlock = ruleList.get(position);
        holder.ruleName.setText(ruleBlock.getName());
        holder.ruleVersion.setText(ruleBlock.getVersion());
        holder.ruleDescription.setText(ruleBlock.getDescription());
    }

    @Override
    public int getItemCount() {
        return ruleList.size();
    }
}
