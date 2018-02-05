package ljw.comicviewer.ui.dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;

/**
 * Created by ljw on 2018-01-24 024.
 */

public class ThemeDialog {
    private Context context;
    private CharSequence[] mItems;
    private CharSequence mMessage;
    private CharSequence mTitle;
    private AlertDialog mAlert;
    private AlertDialog.Builder mBuilder;
    private OnClickListener mItemClickListener;
    private CharSequence mPositiveButtonText;
    private OnButtonClickListener mPositiveButtonListener;
    private CharSequence mNegativeButtonText;
    private OnButtonClickListener mNegativeButtonListener;
    private CharSequence mNeutralButtonText;
    private OnButtonClickListener mNeutralButtonListener;
    private int mCheckedItem;
    private boolean mIsSingleChoice;
    private View mView;
    private View innerView;
    @BindView(R.id.dialog_title)
    TextView vTitle;
    @BindView(R.id.dialog_message)
    TextView vMessage;
    @BindView(R.id.dialog_list)
    ListView vList;
    @BindView(R.id.dialog_contentPanel)
    View vContentPanel;
    @BindView(R.id.dialog_buttonPanel)
    View vButtonPanel;
    @BindView(R.id.dialog_button1)
    Button vButton1;
    @BindView(R.id.dialog_button2)
    Button vButton2;
    @BindView(R.id.dialog_button3)
    Button vButton3;
    @BindView(R.id.dialog_view)
    ViewGroup vView;

    public ThemeDialog(Context context) {
        this.context = context;
        mBuilder = new AlertDialog.Builder(context);
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_list,null);
        ButterKnife.bind(this,mView);
        mBuilder.setView(mView);
    }

    public AlertDialog.Builder getBuilder() {
        return mBuilder;
    }

    public ThemeDialog setTitle(@StringRes int titleId) {
        mTitle = context.getText(titleId);
        vTitle.setText(mTitle);
        return this;
    }

    public ThemeDialog setTitle(CharSequence title){
        mTitle = title.toString();
        vTitle.setText(mTitle);
        return this;
    }

    public ThemeDialog setMessage(@StringRes int messageId){
        mMessage = context.getText(messageId);
        vMessage.setText(mMessage);
        return this;
    }
    public ThemeDialog setMessage(CharSequence msg){
        mMessage = msg.toString();
        vMessage.setText(mMessage);
        return this;
    }

    public ThemeDialog setMessageGravity(int gravity){
        vMessage.setGravity(gravity);
        return this;
    }

    public ThemeDialog setPositiveButton(@StringRes int textId, final OnButtonClickListener listener) {
        mPositiveButtonText = context.getText(textId);
        mPositiveButtonListener = listener;
        return this;
    }

    public ThemeDialog setPositiveButton(CharSequence text, final OnButtonClickListener listener) {
        mPositiveButtonText = text.toString();
        mPositiveButtonListener = listener;
        return this;
    }

    public ThemeDialog setNegativeButton(@StringRes int textId, final OnButtonClickListener listener) {
        mNegativeButtonText = context.getText(textId);
        mNegativeButtonListener = listener;
        return this;
    }

    public ThemeDialog setNegativeButton(CharSequence text, final OnButtonClickListener listener) {
        mNegativeButtonText = text.toString();
        mNegativeButtonListener = listener;
        return this;
    }

    public ThemeDialog setNeutralButton(@StringRes int textId, final OnButtonClickListener listener) {
        mNeutralButtonText = context.getText(textId);
        mNeutralButtonListener =listener;
        return this;
    }

    public ThemeDialog setNeutralButton(CharSequence text, final OnButtonClickListener listener) {
        mNeutralButtonText = text.toString();
        mNeutralButtonListener =listener;
        return this;
    }

    public ThemeDialog setCancelable(boolean cancelable) {
        mBuilder.setCancelable(cancelable);
        return this;
    }

    public ThemeDialog setItems(CharSequence[] items,ThemeDialog.OnClickListener onClickListener) {
        mItems = items;
        mItemClickListener = onClickListener;
        return this;
    }

    public ThemeDialog setSingleChoiceItems(@ArrayRes int itemsId, int checkedItem, final OnClickListener listener) {
        mItems = context.getResources().getTextArray(itemsId);
        mItemClickListener = listener;
        mCheckedItem = checkedItem;
        mIsSingleChoice = true;
        return this;
    }

    public ThemeDialog setSingleChoiceItems(CharSequence[] items, int checkedItem, final OnClickListener listener) {
        mItems = items;
        mItemClickListener = listener;
        mCheckedItem = checkedItem;
        mIsSingleChoice = true;
        return this;
    }

    @BindView(R.id.dialog_radio_group_linear)
    LinearLayout radioGroupLinear;
    private ThemeDialog addRadio(CharSequence text, final int i, final OnClickListener listener){
        View view = LayoutInflater.from(context).inflate(R.layout.item_radio,null);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.item_radio);
        radioButton.setText(text);
        radioButton.setChecked(i == mCheckedItem);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(mAlert,i);
            }
        });
        radioGroupLinear.addView(view);
        return this;
    }

    private int buttonCount = 3;
    private void setButton(Button button, CharSequence text, final OnButtonClickListener listener){
        if (text != null) {
            button.setText(text);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(mAlert);
                }
            });
        }else{
            buttonCount--;
            button.setEnabled(false);
        }
    }

    public ThemeDialog setOnCancelListener(DialogInterface.OnCancelListener onCancelListener){
        mBuilder.setOnCancelListener(onCancelListener);
        return this;
    }

    public ThemeDialog onDismissListener(DialogInterface.OnDismissListener onDismissListener){
        mBuilder.setOnDismissListener(onDismissListener);
        return this;
    }

    public ThemeDialog setView(View view){
        innerView = view;
        try {
            vView.addView(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public ThemeDialog setView(int layoutResId){
        View view = LayoutInflater.from(context).inflate(layoutResId,vView);
        return setView(view);
    }


    public AlertDialog create(){
        vContentPanel.setVisibility(mMessage == null ? View.GONE : View.VISIBLE);
        vList.setVisibility(mItems ==null || mItems.length <= 0 ? View.GONE : View.VISIBLE);
        vTitle.setVisibility(mTitle == null ? View.GONE : View.VISIBLE);
        mAlert = mBuilder.create();
        if(mItems !=null && mItems.length > 0 && mItemClickListener !=null) {
            if (!mIsSingleChoice) {
                vList.setAdapter(new DialogListAdapter(context, mItems));
                vList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mItemClickListener.onClick(mAlert, i);
                    }
                });
            }else{
                for (int i = 0 ; i < mItems.length ; i++){
                    addRadio(mItems[i],i,mItemClickListener);
                }
            }
        }
        setButton(vButton1,mPositiveButtonText,mPositiveButtonListener);
        setButton(vButton2,mNegativeButtonText,mNegativeButtonListener);
        setButton(vButton3,mNeutralButtonText,mNeutralButtonListener);
        if(buttonCount==0){
            vButtonPanel.setVisibility(View.GONE);
        }
        return mAlert;
    }

    public void show(){
        create();
        mAlert.show();
    }

    public boolean isShowing(){
        if(mAlert!=null)
            return mAlert.isShowing();
        return false;
    }

    public void dismiss(){
        if (isShowing()) mAlert.dismiss();
    }

    public interface OnClickListener {
        void onClick(DialogInterface dialog, int i);
    }
    public interface OnButtonClickListener{
        void onClick(DialogInterface dialog);
    }

    class DialogListAdapter extends BaseAdapter {
        private CharSequence[] items;
        private Context context;

        public DialogListAdapter(Context context, CharSequence[] items) {
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
