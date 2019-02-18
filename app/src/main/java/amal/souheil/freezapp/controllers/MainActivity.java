package amal.souheil.freezapp.controllers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import amal.souheil.freezapp.R;
import amal.souheil.freezapp.utils.MyAsyncTask;
import amal.souheil.freezapp.utils.MyAsyncTaskLoader;
import amal.souheil.freezapp.utils.MyHandlerThread;
import amal.souheil.freezapp.utils.Utils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Long>, MyAsyncTask.Listeners {

    //FOR DESIGN
    private ProgressBar progressBar;
    private static int TASK_ID = 100;
    private static int JOBSCHEDULER_ID = 200;

    //FOR DATA
    // 1 - Declaring a HandlerThread
    private MyHandlerThread handlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get progressbar from layout
        this.progressBar = findViewById(R.id.activity_main_progress_bar);

        this.configureHandlerThread();
        //Start AsyncTask
        this.startAsyncTask();

    }

    @Override
    protected void onDestroy() {
        handlerThread.quit(); // QUIT HANDLER THREAD (Free precious resources)
        super.onDestroy();
    }
        // ------------
        // ACTIONS
        // ------------

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

                    break;
                case 40: // CASE USER CLICKED ON BUTTON "STOP ALARM"

                    break;
                case 50: // CASE USER CLICKED ON BUTTON "EXECUTE JOB SCHEDULER"

                    break;
                case 60: // CASE USER CLICKED ON BUTTON "EXECUTE ASYNCTASK"
                    this.startAsyncTask();
                    break;
                case 70: // CASE USER CLICKED ON BUTTON "EXECUTE ASYNCTASKLOADER"

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


    // -------------------------------------------
    // BACKGROUND TASK (HandlerThread & AsyncTask)
    // -------------------------------------------

    // 4-EXECUTE HANDLER THREAD
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
    public void onLoaderReset(Loader<Long> loader) { }

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


