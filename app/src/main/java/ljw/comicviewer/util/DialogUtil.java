package ljw.comicviewer.util;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ljw.comicviewer.R;
import ljw.comicviewer.ui.dialog.BottomDialog;

/**
 * 弹窗工具类
 */

public class DialogUtil {
    //底部弹窗
    public static BottomDialog bulidBottomDialog(Context context){
        BottomDialog bottomDialog = new BottomDialog(context,R.style.bottom_dialog);
        bottomDialog.create();
        bottomDialog.setTitle(R.string.dialog_title_warming);
        bottomDialog.setText(R.string.dialog_content_warming);
        bottomDialog.setTextOk(R.string.dialog_text_ok);
        bottomDialog.setTextCancel(R.string.dialog_text_cancel);
        return bottomDialog;
    }

    public interface OnClickListener {
        void onClick(DialogInterface dialog, int i);
    }

    public static AlertDialog buildThemeDialog(Context context, String titleStr,
                                               String[] items, final OnClickListener onClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_list,null);
        TextView title = (TextView) view.findViewById(R.id.dialog_title);
        title.setText(titleStr);
        ListView listView = (ListView) view.findViewById(R.id.dialog_list);
        listView.setAdapter(new DialogListAdapter(context,items));
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onClickListener.onClick(alertDialog,i);
            }
        });
        return alertDialog;
    }

    static class DialogListAdapter extends BaseAdapter{
        private String[] items;
        private Context context;

        public DialogListAdapter(Context context, String[] items) {
            this.items = items;
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(context);
            textView.setText(items[i]);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(0,30,0,30);
            return textView;
        }

    }

}
