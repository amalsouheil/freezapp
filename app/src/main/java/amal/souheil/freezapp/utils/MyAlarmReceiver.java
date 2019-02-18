package amal.souheil.freezapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Souheil Amal on 2019-02-15
 */
public class MyAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TAG", "Alarm launched !");
        Toast.makeText(context, "We love you so much guys...", Toast.LENGTH_SHORT).show();

    }
}
