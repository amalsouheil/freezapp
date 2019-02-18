package amal.souheil.freezapp.controllers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import amal.souheil.freezapp.R;
import amal.souheil.freezapp.utils.MyAlarmReceiver;
import amal.souheil.freezapp.utils.MyAsyncTask;
import amal.souheil.freezapp.utils.MyAsyncTaskLoader;
import amal.souheil.freezapp.utils.MyHandlerThread;
import amal.souheil.freezapp.utils.SyncJobService;
import amal.souheil.freezapp.utils.Utils;

public class MainActivity extends AppCompatActivity implements  MyAsyncTask.Listeners,LoaderManager.LoaderCallbacks<Long> {

    //FOR DESIGN
    private ProgressBar progressBar;
    // 3 - Create static task id that will identify our loader
    private static int TASK_ID = 100;

    // Creating an intent to execute our broadcast
    private PendingIntent pendingIntent;

    // 1 - Create an ID for JobScheduler
    private static int JOBSCHEDULER_ID = 200;

    //FOR DATA
    private MyHandlerThread handlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get progressbar from layout
        this.progressBar = findViewById(R.id.activity_main_progress_bar);

        //Configure Handler Thread
        this.configureHandlerThread();

        //Try to resume possible loading AsyncTask
        this.resumeAsyncTaskLoaderIfPossible();

        //Configuring The AlarmManager
        this.configureAlarmManager();
        

    }



    @Override
    protected void onDestroy() {
        handlerThread.quit(); // QUIT HANDLER THREAD (Free precious resources)
        super.onDestroy();
    }
        // ------------
        // ACTIONS
    // ------------

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onClickButton (View v){
            int buttonTag = Integer.valueOf(v.getTag().toString());
            switch (buttonTag) {
                case 10: // CASE USER CLICKED ON BUTTON "EXECUTE ACTION IN MAIN THREAD"
                    Utils.executeLongActionDuring7seconds();
                    break;
                case 20: // CASE USER CLICKED ON BUTTON "EXECUTE ACTION IN BACKGROUND"
                    this.startHandlerThread();
                    break;
                case 30: // CASE USER CLICKED ON BUTTON "START ALARM"
                    this.startAlarm();
                    break;
                case 40: // CASE USER CLICKED ON BUTTON "STOP ALARM"
                    this.stopAlarm();
                    break;
                case 50: // CASE USER CLICKED ON BUTTON "EXECUTE JOB SCHEDULER"
                    this.startJobScheduler();
                    break;
                case 60: // CASE USER CLICKED ON BUTTON "EXECUTE ASYNCTASK"
                    this.startAsyncTask();
                    break;
                case 70: // CASE USER CLICKED ON BUTTON "EXECUTE ASYNCTASKLOADER"
                    this.startAsyncTaskLoader();
                    break;
            }
        }
    // -----------------
    // CONFIGURATION
    // -----------------

    // Configuring the HandlerThread
    private void configureHandlerThread(){
        handlerThread = new MyHandlerThread("MyAwesomeHanderThread", this.progressBar);
    }


    // 2 - Configuring the AlarmManager
    private void configureAlarmManager(){
        Intent alarmIntent = new Intent(MainActivity.this, MyAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    // ---------------------------------------------
    // SCHEDULE TASK (AlarmManager & JobScheduler)
    // ---------------------------------------------

    // 3 - Start Alarm
    private void startAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,0, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        Toast.makeText(this, "Alarm set !", Toast.LENGTH_SHORT).show();
    }

    // 4 - Stop Alarm
    private void stopAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled !", Toast.LENGTH_SHORT).show();
    }

    // 2 - Start service (job) from the JobScheduler
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startJobScheduler(){
        // 2.1 Create a builder that will build an object JobInfo containing launching conditions and the service
        JobInfo job = new JobInfo.Builder(JOBSCHEDULER_ID, new ComponentName(this, SyncJobService.class))
                .setRequiresCharging(true) // The job will be executed if charging
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // The job will be executed if any network is available
                .setPeriodic(3600000) // The job will be executed each hour
                .build();

        // 2.2 - Get the JobScheduler and schedule the previous job
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(job);
    }

    // 3 - Stop service (job) from the JobScheduler
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopJobScheduler(){
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOBSCHEDULER_ID);
    }


    // -------------------------------------------
    // BACKGROUND TASK (HandlerThread & AsyncTask)
    // -------------------------------------------

    // EXECUTE HANDLER THREAD
    private void startHandlerThread(){
        handlerThread.startHandler();
    }
    // ------

    // 3 - We create and start our AsyncTask
    private void startAsyncTask() {
        new MyAsyncTask(this).execute();
    }

    // 2 - Override methods of callback
    @Override
    public void onPreExecute() {
        // 2.1 - We update our UI before task (starting ProgressBar)
        this.updateUIBeforeTask();
    }

    @Override
    public void doInBackground() { }

    @Override
    public void onPostExecute(Long taskEnd) {
        // 2.2 - We update our UI before task (stopping ProgressBar)
        this.updateUIAfterTask(taskEnd);
    }

    //-------
    // 4 - Start a new AsyncTaskLoader
    private void startAsyncTaskLoader(){
        getSupportLoaderManager().restartLoader(TASK_ID, null, this);
    }

    // 7 - Resume previous AsyncTaskLoader if still running
    private void resumeAsyncTaskLoaderIfPossible(){
        if (getSupportLoaderManager().getLoader(TASK_ID) != null && getSupportLoaderManager().getLoader(TASK_ID).isStarted()) {
            getSupportLoaderManager().initLoader(TASK_ID, null, this);
            this.updateUIBeforeTask();
        }
    }


    // 2 - Implements callback methods
    @Override
    public Loader<Long> onCreateLoader(int id, Bundle args) {
        Log.e("TAG", "On Create !");
        this.updateUIBeforeTask();
        return new MyAsyncTaskLoader(this); // 5 - Return a new AsyncTaskLoader
    }

    @Override
    public void onLoadFinished(Loader<Long> loader, Long data) {
        Log.e("TAG", "On Finished !");
        loader.stopLoading(); // 6 - Force loader to stop
        updateUIAfterTask(data);
    }

    @Override
    public void onLoaderReset(Loader<Long> loader) {

    }
    // -----------------
    // UPDATE UI
    // -----------------

    public void updateUIBeforeTask(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void updateUIAfterTask(Long taskEnd){
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Task is finally finished at : "+taskEnd+" !", Toast.LENGTH_SHORT).show();
    }
}


