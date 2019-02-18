package amal.souheil.freezapp.utils;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by Souheil Amal on 2019-02-18
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SyncJobService extends JobService implements MyAsyncTask.Listeners {

    private MyAsyncTask jobTask;
    private JobParameters jobParameters;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.e(this.getClass().getSimpleName(), "SyncJob is started.");
        this.jobParameters = jobParameters;
        this.jobTask = new MyAsyncTask(this);
        this.jobTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {
        Log.e(this.getClass().getSimpleName(), "SyncJob is stopped !");
        if (this.jobTask != null) this.jobTask.cancel(true);
        return false;
    }

    @Override
    public void onPreExecute() {  }

    @Override
    public void doInBackground() { }

    @Override
    public void onPostExecute(Long taskEnd) {
        Log.e("TAG", "Task ended at : "+taskEnd);
        jobFinished(jobParameters, false);
    }
}
