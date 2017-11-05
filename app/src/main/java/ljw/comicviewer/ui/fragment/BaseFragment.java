package ljw.comicviewer.ui.fragment;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

/**
 * Created by ljw on 2017-11-05 005.
 */

public class BaseFragment extends Fragment{

    public void initLoad() {}

    public void initView() {}

    //界面异步更新
    public Object myDoInBackground(String TAG,Object obj){return null;}

    public void myOnPostExecute(String TAG,Object resultObj){}

    public class UIUpdateTask extends AsyncTask<Void,Void,Object>{
        private String tag;
        private Object obj;

        public UIUpdateTask(String tag, Object obj) {
            this.tag = tag;
            this.obj = obj;
        }


        @Override
        protected Object doInBackground(Void... voids) {
            return myDoInBackground(tag,obj);
        }

        @Override
        protected void onPostExecute(Object obj) {
            super.onPostExecute(obj);
            myOnPostExecute(tag,obj);
        }
    }

}
