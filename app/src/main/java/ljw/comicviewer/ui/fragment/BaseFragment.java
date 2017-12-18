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
    public Object myDoInBackground(String what,Object obj){return null;}

    public void myOnPostExecute(String what,Object resultObj){}

    public class UIUpdateTask extends AsyncTask<Void,Void,Object>{
        private String what;
        private Object obj;

        public UIUpdateTask(String what, Object obj) {
            this.what = what;
            this.obj = obj;
        }


        @Override
        protected Object doInBackground(Void... voids) {
            return myDoInBackground(what,obj);
        }

        @Override
        protected void onPostExecute(Object obj) {
            super.onPostExecute(obj);
            myOnPostExecute(what,obj);
        }
    }

}
