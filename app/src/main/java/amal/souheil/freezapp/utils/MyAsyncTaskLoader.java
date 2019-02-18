package amal.souheil.freezapp.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by Souheil Amal on 2019-02-15
 */
public class MyAsyncTaskLoader extends AsyncTaskLoader<Long> {

    public MyAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    public Long loadInBackground() {
        return Utils.executeLongActionDuring7seconds();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
